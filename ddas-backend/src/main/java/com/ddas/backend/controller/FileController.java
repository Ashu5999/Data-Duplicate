package com.ddas.backend.controller;

import com.ddas.backend.model.FileData;
import com.ddas.backend.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/files")
@CrossOrigin
public class FileController {

    @Autowired
    private FileRepository repository;

    @GetMapping
    public List<FileData> getAllFiles(@RequestParam(required = false) String uploadedBy) {
        if (uploadedBy != null && !uploadedBy.trim().isEmpty()) {
            return repository.findByUploadedBy(uploadedBy);
        }
        return repository.findAll();
    }

    @PostMapping
    public Map<String, String> addFile(
            @RequestParam("file")       MultipartFile file,
            @RequestParam("fileName")   String fileName,
            @RequestParam("fileHash")   String fileHash,
            @RequestParam("uploadedBy") String uploadedBy) {

        Map<String, String> response = new HashMap<>();

        // Duplicate detection — uses the correct repository method name
        if (repository.existsByFileHash(fileHash)) {
            response.put("status", "error");
            response.put("message", "Duplicate file detected!");
            return response;
        }

        // Build FileData from individual params
        FileData fileData = new FileData();
        fileData.setFileName(fileName);
        fileData.setFileHash(fileHash);
        fileData.setUploadedBy(uploadedBy);
        fileData.setFileSize(file.getSize());
        fileData.setCreatedAt(LocalDateTime.now());
        
        try {
            fileData.setFileContent(file.getBytes());
        } catch (java.io.IOException e) {
            response.put("status", "error");
            response.put("message", "Failed to read file content");
            return response;
        }

        repository.save(fileData);

        response.put("status", "success");
        response.put("message", "File added successfully");

        return response;
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        FileData fileData = repository.findById(id).orElse(null);
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

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileData.getFileContent());
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewFile(@PathVariable Long id) {
        FileData fileData = repository.findById(id).orElse(null);
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

        String fileName = fileData.getFileName();
        MediaType mediaType = org.springframework.http.MediaTypeFactory.getMediaType(fileName)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        // Force text/plain for text-like files so the browser renders them inline
        String ext = "";
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0) {
            ext = fileName.substring(dotIdx + 1).toLowerCase();
        }
        if (ext.equals("csv") || ext.equals("txt") || ext.equals("log") || ext.equals("json") || ext.equals("xml")) {
            mediaType = MediaType.TEXT_PLAIN;
        } else if (ext.equals("pdf")) {
            mediaType = MediaType.APPLICATION_PDF;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .contentType(mediaType)
                .body(fileData.getFileContent());
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteFile(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        if (repository.existsById(id)) {
            repository.deleteById(id);
            response.put("status", "success");
            response.put("message", "File deleted successfully");
        } else {
            response.put("status", "error");
            response.put("message", "File not found");
        }
        return response;
    }

    @PostMapping("/check")
    public Map<String, Object> checkDuplicate(@RequestBody Map<String, String> body) {
        String keyword = body.getOrDefault("keyword", "").trim();

        Map<String, Object> response = new HashMap<>();

        if (keyword.isEmpty()) {
            response.put("isDuplicate", false);
            response.put("message", "Please provide a keyword to search.");
            return response;
        }

        // Check by exact hash match first, then by name contains
        List<FileData> matches = new java.util.ArrayList<>();
        for (FileData file : repository.findAll()) {
            if (file.getFileHash().equalsIgnoreCase(keyword)
                    || file.getFileName().toLowerCase().contains(keyword.toLowerCase())) {
                matches.add(file);
            }
        }

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