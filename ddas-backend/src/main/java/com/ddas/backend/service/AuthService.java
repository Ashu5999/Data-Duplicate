package com.ddas.backend.service;

// ============================================================
// AuthService.java  —  Package: com.ddas.backend.service
// ------------------------------------------------------------
// PURPOSE:
//   Contains ALL business logic for user authentication.
//   The controller (AuthController) simply calls these methods
//   and returns the result — it never touches the DB directly.
//
// RESPONSIBILITIES:
//   1. login()         → Verify email + password, return status map
//   2. register()      → Validate input, check duplicate email,
//                        hash password, save new User
//   3. resetPassword() → Find user by email, hash & save new password
//
// LAYER: Service  (sits between Controller and Repository)
// ============================================================

import com.ddas.backend.model.User;
import com.ddas.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service  // Tells Spring to create a singleton bean of this class
public class AuthService {

    // ── Dependencies ────────────────────────────────────────────
    // Spring injects the UserRepository bean automatically
    @Autowired
    private UserRepository userRepository;

    // BCrypt is the industry-standard one-way password hashing algorithm.
    // Passwords are NEVER stored in plain text.
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // ────────────────────────────────────────────────────────────


    /**
     * LOGIN
     *
     * Flow:
     *  1. Look up the user by email in the database.
     *  2. If found, compare the plain-text password against the stored BCrypt hash.
     *  3. Return a response map with "status" and "message" keys.
     *
     * @param email    The email address entered by the user
     * @param password The plain-text password entered by the user
     * @return Map with keys: "status" ("success"/"error"), "message", "email" (on success)
     */
    public Map<String, String> login(String email, String password) {
        Map<String, String> response = new HashMap<>();

        // Try to find a user with the given email
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            // Credentials valid → return success with the user's email
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("email", userOpt.get().getEmail());
        } else {
            // Either email not found OR password hash doesn't match
            response.put("status", "error");
            response.put("message", "Invalid email or password");
        }

        return response;
    }


    /**
     * REGISTER
     *
     * Flow:
     *  1. Validate that email is non-empty and password is ≥6 chars.
     *  2. Check that the email is not already registered.
     *  3. Hash the password with BCrypt.
     *  4. Create a new User entity and save it to the database.
     *
     * @param email    The desired email address (must be unique)
     * @param password The desired password (minimum 6 characters)
     * @return Map with keys: "status" ("success"/"error"), "message"
     */
    public Map<String, String> register(String email, String password) {
        Map<String, String> response = new HashMap<>();

        // ── Input Validation ────────────────────────────────────
        if (email == null || email.isBlank() || password == null || password.length() < 6) {
            response.put("status", "error");
            response.put("message", "Email is required and password must be at least 6 characters");
            return response;
        }

        // ── Duplicate Check ─────────────────────────────────────
        // Prevent two accounts from using the same email address
        if (userRepository.existsByEmail(email)) {
            response.put("status", "error");
            response.put("message", "An account with this email already exists");
            return response;
        }

        // ── Create & Save User ──────────────────────────────────
        // Password is BCrypt-hashed before being stored
        User user = new User(email, passwordEncoder.encode(password));
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "Account created! You can now log in.");
        return response;
    }


    /**
     * RESET PASSWORD
     *
     * Flow:
     *  1. Validate that email and newPassword are valid.
     *  2. Find the user by email (must already exist).
     *  3. Hash the new password and update the record in the DB.
     *
     * @param email       The registered email address of the account
     * @param newPassword The new password to set (minimum 6 characters)
     * @return Map with keys: "status" ("success"/"error"), "message"
     */
    public Map<String, String> resetPassword(String email, String newPassword) {
        Map<String, String> response = new HashMap<>();

        // ── Input Validation ────────────────────────────────────
        if (email == null || email.isBlank() || newPassword == null || newPassword.length() < 6) {
            response.put("status", "error");
            response.put("message", "Email is required and password must be at least 6 characters");
            return response;
        }

        // ── Find Existing Account ───────────────────────────────
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No account found with that email address");
            return response;
        }

        // ── Update Password ─────────────────────────────────────
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword)); // Hash before saving
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "Password reset successfully! You can now log in.");
        return response;
    }
}
