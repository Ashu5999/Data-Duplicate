package com.ddas.backend.service;

// ============================================================
// FileService.java  —  Package: com.ddas.backend.service
// ------------------------------------------------------------
// PURPOSE:
//   Contains ALL business logic for file operations.
//   The controller (FileController) only handles HTTP
//   concerns (request params, response codes) and delegates
//   every real decision to this service.
//
// RESPONSIBILITIES:
//   1. getAllFiles()    → Return all files, or filter by uploader email
//   2. addFile()       → Run duplicate-hash detection, build FileData
//                        entity, store file bytes, save to DB
//   3. downloadFile()  → Fetch file bytes for download (attachment)
//   4. viewFile()      → Fetch file bytes for inline browser viewing
//   5. deleteFile()    → Remove a file record from the DB by ID
//   6. checkDuplicate()→ Search DB by hash or filename keyword
//
// LAYER: Service  (sits between Controller and Repository)
// ============================================================

import com.ddas.backend.model.FileData;
import com.ddas.backend.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service  // Marks this class as a Spring-managed service bean
public class FileService {

    // ── Dependency ───────────────────────────────────────────────
    // Spring injects the FileRepository (JPA) bean automatically
    @Autowired
    private FileRepository fileRepository;
    // ────────────────────────────────────────────────────────────


    /**
     * GET ALL FILES  (with optional filter)
     *
     * If uploadedBy is provided and non-empty, returns only the files
     * belonging to that user's email address.
     * Otherwise, returns the full list from the database.
     *
     * Used by: Dashboard page to list "my files"
     *
     * @param uploadedBy  Email address to filter by (nullable / optional)
     * @return List of FileData entities (may be empty, never null)
     */
    public List<FileData> getAllFiles(String uploadedBy) {
        if (uploadedBy != null && !uploadedBy.trim().isEmpty()) {
            // Custom query defined in FileRepository interface
            return fileRepository.findByUploadedBy(uploadedBy);
        }
        // No filter → return everything
        return fileRepository.findAll();
    }


    /**
     * ADD / UPLOAD FILE
     *
     * Flow:
     *  1. Check if a file with the same SHA-256 hash already exists
     *     in the database → if yes, reject (duplicate detected).
     *  2. Build a FileData entity from the request params.
     *  3. Read the raw file bytes from the MultipartFile and store them.
     *  4. Save the entity to the H2 database.
     *
     * @param file        The uploaded file (Spring MultipartFile)
     * @param fileName    Display name of the file
     * @param fileHash    SHA-256 hash computed on the frontend (hex string)
     * @param uploadedBy  Email of the user performing the upload
     * @return Response map with "status" ("success"/"error") and "message"
     */
    public Map<String, String> addFile(MultipartFile file, String fileName,
                                       String fileHash, String uploadedBy) {
        Map<String, String> response = new HashMap<>();

        // ── Duplicate Detection ─────────────────────────────────
        // If this exact hash already exists, refuse the upload
        if (fileRepository.existsByFileHash(fileHash)) {
            response.put("status", "error");
            response.put("message", "Duplicate file detected!");
            return response;
        }

        // ── Build FileData Entity ───────────────────────────────
        FileData fileData = new FileData();
        fileData.setFileName(fileName);
        fileData.setFileHash(fileHash);
        fileData.setUploadedBy(uploadedBy);
        fileData.setFileSize(file.getSize());            // Size in bytes
        fileData.setCreatedAt(LocalDateTime.now());      // Server-side timestamp

        // ── Read File Bytes ─────────────────────────────────────
        // Store the raw content as a BLOB so it can be downloaded later
        try {
            fileData.setFileContent(file.getBytes());
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Failed to read file content");
            return response;
        }

        // ── Persist to Database ─────────────────────────────────
        fileRepository.save(fileData);

        response.put("status", "success");
        response.put("message", "File added successfully");
        return response;
    }


    /**
     * DOWNLOAD FILE
     *
     * Retrieves the stored file bytes and wraps them in a ResponseEntity
     * with "Content-Disposition: attachment" — this forces the browser
     * to download the file instead of trying to display it.
     *
     * @param id  The database primary key (auto-generated Long) of the file
     * @return 200 OK with file bytes, or 404 HTML error page if not found
     */
    public ResponseEntity<?> downloadFile(Long id) {
        FileData fileData = fileRepository.findById(id).orElse(null);

        // ── Not Found Guard ─────────────────────────────────────
        if (fileData == null || fileData.getFileContent() == null) {
            String htmlError = "<html><body style='font-family:sans-serif; text-align:center; padding: 50px;'>" +
                               "<h2>File Not Found</h2>" +
                               "<p>This file has no stored content. It was likely uploaded before the download feature was enabled.</p>" +
                               "<button onclick='window.history.back()'>Go Back</button>" +
                               "</body></html>";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body(htmlError.getBytes());
        }

        // Return file bytes as a downloadable attachment
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileData.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData.getFileContent());
    }


    /**
     * VIEW FILE  (inline browser preview)
     *
     * Similar to downloadFile(), but uses "Content-Disposition: inline"
     * so the browser tries to display the content (e.g. PDF viewer,
     * text display) instead of downloading it.
     *
     * Special MIME-type handling:
     *  - .csv, .txt, .log, .json, .xml → forced to text/plain for rendering
     *  - .pdf                           → application/pdf for native viewer
     *  - everything else                → Spring auto-detects from filename
     *
     * @param id  The database primary key of the file
     * @return 200 OK with file bytes + correct Content-Type, or 404 on error
     */
    public ResponseEntity<?> viewFile(Long id) {
        FileData fileData = fileRepository.findById(id).orElse(null);

        // ── Not Found Guard ─────────────────────────────────────
        if (fileData == null || fileData.getFileContent() == null) {
            String htmlError = "<html><body style='font-family:sans-serif; text-align:center; padding: 50px;'>" +
                               "<h2>File Not Found</h2>" +
                               "<p>This file has no stored content. It was likely uploaded before the view feature was enabled.</p>" +
                               "<button onclick='window.close()'>Close</button>" +
                               "</body></html>";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body(htmlError.getBytes());
        }

        // ── Determine MIME Type from file extension ─────────────
        String fileName = fileData.getFileName();
        MediaType mediaType = org.springframework.http.MediaTypeFactory
                .getMediaType(fileName)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        String ext = "";
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0) {
            ext = fileName.substring(dotIdx + 1).toLowerCase();
        }

        // Override MIME type for specific text-like formats
        if (ext.equals("csv") || ext.equals("txt") || ext.equals("log")
                || ext.equals("json") || ext.equals("xml")) {
            mediaType = MediaType.TEXT_PLAIN;
        } else if (ext.equals("pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        }

        // Return file bytes for inline display
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + fileName + "\"")
                .contentType(mediaType)
                .body(fileData.getFileContent());
    }


    /**
     * DELETE FILE
     *
     * Removes a file record from the database by its ID.
     * The stored BLOB content is also removed since it is part of
     * the same FileData entity (no external file storage).
     *
     * @param id  The database primary key of the file to delete
     * @return Response map with "status" and "message"
     */
    public Map<String, String> deleteFile(Long id) {
        Map<String, String> response = new HashMap<>();

        if (fileRepository.existsById(id)) {
            fileRepository.deleteById(id);
            response.put("status", "success");
            response.put("message", "File deleted successfully");
        } else {
            response.put("status", "error");
            response.put("message", "File not found");
        }
        return response;
    }


    /**
     * CHECK DUPLICATE  (keyword search)
     *
     * Searches the database for files matching a given keyword.
     * Matching logic:
     *  - Exact match on fileHash (case-insensitive)
     *  - OR file name contains the keyword (case-insensitive)
     *
     * Returns whether any duplicates were found, a human-readable
     * message, and the list of matching FileData objects if any.
     *
     * Used by: Dashboard "Check Duplicate" panel
     *
     * @param body  JSON body map expected to contain key "keyword"
     * @return Response map with "isDuplicate" (Boolean), "message",
     *         and optionally "matches" (List of FileData)
     */
    public Map<String, Object> checkDuplicate(Map<String, String> body) {
        String keyword = body.getOrDefault("keyword", "").trim();
        Map<String, Object> response = new HashMap<>();

        // ── Empty keyword guard ─────────────────────────────────
        if (keyword.isEmpty()) {
            response.put("isDuplicate", false);
            response.put("message", "Please provide a keyword to search.");
            return response;
        }

        // ── Search by hash (exact) or name (contains) ───────────
        List<FileData> matches = new ArrayList<>();
        for (FileData file : fileRepository.findAll()) {
            if (file.getFileHash().equalsIgnoreCase(keyword)
                    || file.getFileName().toLowerCase().contains(keyword.toLowerCase())) {
                matches.add(file);
            }
        }

        // ── Build response ──────────────────────────────────────
        if (matches.isEmpty()) {
            response.put("isDuplicate", false);
            response.put("message", "No duplicates found! Safe to download.");
        } else {
            response.put("isDuplicate", true);
            response.put("message", "⚠️ Found " + matches.size() + " matching file(s) already registered.");
            response.put("matches", matches);
        }

        return response;
    }
}
