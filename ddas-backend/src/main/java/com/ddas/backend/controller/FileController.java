package com.ddas.backend.controller;

import com.ddas.backend.model.FileData;
import com.ddas.backend.repository.FileRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/files")
@CrossOrigin
public class FileController {

    private FileRepository repository = new FileRepository();

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

        // Duplicate detection — unchanged
        if (repository.existsByHash(fileHash)) {
            response.put("status", "error");
            response.put("message", "Duplicate file detected!");
            return response;
        }

        // Build FileData from individual params
        FileData fileData = new FileData();
        fileData.setFileName(fileName);
        fileData.setFileHash(fileHash);
        fileData.setUploadedBy(uploadedBy);

        repository.save(fileData);

        response.put("status", "success");
        response.put("message", "File added successfully");

        return response;
    }
}