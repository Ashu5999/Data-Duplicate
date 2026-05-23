package com.ddas.backend.repository;

// ============================================================
// UserRepository.java  —  Package: com.ddas.backend.repository
// ------------------------------------------------------------
// PURPOSE:
//   Data access layer for the "users" table.
//   Extends Spring Data JPA's JpaRepository, which automatically
//   provides standard CRUD operations (save, findById, findAll,
//   deleteById, existsById, etc.) with NO extra code needed.
//
// CUSTOM QUERY METHODS (auto-implemented by Spring Data JPA):
//   findByEmail(email)     → Looks up a user by their email address.
//                            Returns Optional<User> (empty if not found).
//                            Used for login and password reset.
//
//   existsByEmail(email)   → Returns true if an account with that email
//                            already exists.
//                            Used during registration to prevent duplicates.
//
// JpaRepository<User, Long>:
//   User = the entity class this repository manages
//   Long = the type of the primary key (@Id field)
//
// USED BY:
//   AuthService → for all user queries and persistence operations
// ============================================================

import com.ddas.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository  // Marks this as a Spring-managed repository bean
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * Spring Data JPA auto-generates the SQL:
     *   SELECT * FROM users WHERE email = ? LIMIT 1
     *
     * Returns Optional<User> — callers must handle the case where
     * no user with that email exists.
     *
     * Used in:
     *   AuthService.login()         → verify credentials
     *   AuthService.resetPassword() → confirm account exists before resetting
     *
     * @param email  The email address to search for
     * @return Optional containing the User if found, or empty Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email already exists.
     *
     * Spring Data JPA auto-generates the SQL:
     *   SELECT COUNT(*) > 0 FROM users WHERE email = ?
     *
     * Used in AuthService.register() to prevent two accounts
     * from sharing the same email address.
     *
     * @param email  The email address to check
     * @return true if an account with this email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
