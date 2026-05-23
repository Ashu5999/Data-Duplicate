package com.ddas.backend.controller;

// ============================================================
// FileController.java  —  Package: com.ddas.backend.controller
// ------------------------------------------------------------
// PURPOSE:
//   HTTP entry point for all file-related API calls.
//   This controller is intentionally THIN — it only:
//     1. Maps HTTP routes to methods
//     2. Receives request parameters / body from the frontend
//     3. Delegates ALL business logic to FileService
//     4. Returns the response (JSON or file bytes) from the service
//
// BASE URL:  /api/files
//
// ENDPOINTS:
//   GET    /api/files                → Get all files (optionally filtered by uploader)
//   POST   /api/files                → Upload a new file (with duplicate check)
//   GET    /api/files/download/{id}  → Download a file as attachment
//   GET    /api/files/view/{id}      → View a file inline in the browser
//   DELETE /api/files/{id}           → Delete a file by ID
//   POST   /api/files/check          → Check if a file keyword/hash is a duplicate
//
// CALLED BY:
//   Dashboard.html   (list, delete, view, download files)
//   Upload.html      (upload files, check duplicates)
// ============================================================

import com.ddas.backend.model.FileData;
import com.ddas.backend.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController                    // Returns JSON responses (not HTML views)
@RequestMapping("/api/files")      // All routes prefixed with /api/files
@CrossOrigin                       // Allow CORS from any origin (HTML frontend)
public class FileController {

    // ── Dependency ───────────────────────────────────────────────
    // FileService handles all business logic for file operations
    @Autowired
    private FileService fileService;
    // ────────────────────────────────────────────────────────────


    /**
     * GET /api/files
     * GET /api/files?uploadedBy=user@email.com
     *
     * Returns a list of all uploaded files.
     * If the "uploadedBy" query parameter is provided, only returns
     * files uploaded by that specific user (email).
     *
     * Response JSON: Array of FileData objects
     * Used by: Dashboard.html to populate "My Files" table
     */
    @GetMapping
    public List<FileData> getAllFiles(@RequestParam(required = false) String uploadedBy) {
        return fileService.getAllFiles(uploadedBy);
    }


    /**
     * POST /api/files
     *
     * Uploads a new file to the system.
     * Performs SHA-256 duplicate detection before saving.
     *
     * Request (multipart/form-data):
     *   file        - The actual file binary
     *   fileName    - The display name for the file
     *   fileHash    - SHA-256 hash string (computed in the browser)
     *   uploadedBy  - Email address of the uploader
     *
     * Response JSON:
     *   { "status": "success", "message": "File added successfully" }
     *   { "status": "error",   "message": "Duplicate file detected!" }
     *
     * Used by: Upload.html
     */
    @PostMapping
    public Map<String, String> addFile(
            @RequestParam("file")       MultipartFile file,
            @RequestParam("fileName")   String fileName,
            @RequestParam("fileHash")   String fileHash,
            @RequestParam("uploadedBy") String uploadedBy) {

        return fileService.addFile(file, fileName, fileHash, uploadedBy);
    }


    /**
     * GET /api/files/download/{id}
     *
     * Serves the file bytes as a downloadable attachment.
     * The browser will show a "Save As" dialog.
     *
     * Path Variable:
     *   id - The database ID (Long) of the file to download
     *
     * Response: Raw file bytes with Content-Disposition: attachment
     * Used by: Dashboard.html "Download" button
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) {
        return fileService.downloadFile(id);
    }


    /**
     * GET /api/files/view/{id}
     *
     * Serves the file bytes for inline browser viewing.
     * PDFs open in the browser's PDF viewer.
     * Text files (CSV, TXT, JSON, etc.) render as plain text.
     *
     * Path Variable:
     *   id - The database ID (Long) of the file to view
     *
     * Response: Raw file bytes with Content-Disposition: inline
     * Used by: Dashboard.html "View" button
     */
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewFile(@PathVariable Long id) {
        return fileService.viewFile(id);
    }


    /**
     * DELETE /api/files/{id}
     *
     * Permanently removes a file record (and its stored content)
     * from the database.
     *
     * Path Variable:
     *   id - The database ID (Long) of the file to delete
     *
     * Response JSON:
     *   { "status": "success", "message": "File deleted successfully" }
     *   { "status": "error",   "message": "File not found" }
     *
     * Used by: Dashboard.html "Delete" button
     */
    @DeleteMapping("/{id}")
    public Map<String, String> deleteFile(@PathVariable Long id) {
        return fileService.deleteFile(id);
    }


    /**
     * POST /api/files/check
     *
     * Checks if a file with the given keyword (hash or name) already
     * exists in the database. Used for manual duplicate checking.
     *
     * Request Body (JSON):
     *   { "keyword": "search-term-or-hash" }
     *
     * Response JSON:
     *   { "isDuplicate": false, "message": "No duplicates found! Safe to download." }
     *   { "isDuplicate": true,  "message": "⚠️ Found N matching file(s)...", "matches": [...] }
     *
     * Used by: Dashboard.html "Check Duplicate" panel
     */
    @PostMapping("/check")
    public Map<String, Object> checkDuplicate(@RequestBody Map<String, String> body) {
        return fileService.checkDuplicate(body);
    }
}