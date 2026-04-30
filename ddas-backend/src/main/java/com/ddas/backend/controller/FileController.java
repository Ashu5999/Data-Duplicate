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

@RestController
@RequestMapping("/api/files")
@CrossOrigin
public class FileController {

    @Autowired
    private FileRepository repository;

    @GetMapping
    public List<FileData> getAllFiles() {
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

        repository.save(fileData);

        response.put("status", "success");
        response.put("message", "File added successfully");

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