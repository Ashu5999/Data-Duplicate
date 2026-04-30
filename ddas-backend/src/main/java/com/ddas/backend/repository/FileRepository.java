package com.ddas.backend.repository;

import com.ddas.backend.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileData, Long> {
    boolean existsByFileHash(String fileHash);
    List<FileData> findByUploadedBy(String uploadedBy);
}