package com.ddas.backend.repository;

import com.ddas.backend.model.FileData;
import java.util.ArrayList;
import java.util.List;

public class FileRepository {

    private List<FileData> files = new ArrayList<>();

    public boolean existsByHash(String hash) {
        for (FileData file : files) {
            if (file.getFileHash().equals(hash)) {
                return true;
            }
        }
        return false;
    }

    public void save(FileData file) {
        files.add(file);
    }

    public List<FileData> findAll() {
        return files;
    }
}