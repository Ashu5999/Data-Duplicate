package com.ddas.backend.repository;

// ============================================================
// FileRepository.java  —  Package: com.ddas.backend.repository
// ------------------------------------------------------------
// PURPOSE:
//   Data access layer for the "file_data" table.
//   Extends Spring Data JPA's JpaRepository, which automatically
//   provides standard CRUD operations (save, findById, findAll,
//   deleteById, existsById, etc.) with NO extra code needed.
//
// CUSTOM QUERY METHODS (auto-implemented by Spring Data JPA):
//   existsByFileHash(hash)      → Returns true if a file with
//                                 that exact SHA-256 hash exists.
//                                 Used for duplicate detection on upload.
//
//   findByUploadedBy(email)     → Returns all files uploaded by
//                                 a specific user email.
//                                 Used by Dashboard to show "my files".
//
// JpaRepository<FileData, Long>:
//   FileData = the entity class this repository manages
//   Long     = the type of the primary key (@Id field)
//
// USED BY:
//   FileService → for all file queries and persistence operations
// ============================================================

import com.ddas.backend.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository  // Marks this as a Spring-managed repository bean
public interface FileRepository extends JpaRepository<FileData, Long> {

    /**
     * Checks if a file with the given SHA-256 hash already exists.
     *
     * Spring Data JPA auto-generates the SQL:
     *   SELECT COUNT(*) > 0 FROM file_data WHERE file_hash = ?
     *
     * Used in FileService.addFile() to prevent duplicate uploads.
     *
     * @param fileHash  SHA-256 hex string to search for
     * @return true if a matching file exists, false otherwise
     */
    boolean existsByFileHash(String fileHash);

    /**
     * Returns all files uploaded by a specific user (by email).
     *
     * Spring Data JPA auto-generates the SQL:
     *   SELECT * FROM file_data WHERE uploaded_by = ?
     *
     * Used in FileService.getAllFiles() when the dashboard filters
     * the file list for the currently logged-in user.
     *
     * @param uploadedBy  The email address of the uploader
     * @return List of matching FileData entities (empty list if none)
     */
    List<FileData> findByUploadedBy(String uploadedBy);
}