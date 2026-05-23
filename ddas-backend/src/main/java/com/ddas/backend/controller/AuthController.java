package com.ddas.backend.controller;

// ============================================================
// AuthController.java  —  Package: com.ddas.backend.controller
// ------------------------------------------------------------
// PURPOSE:
//   HTTP entry point for all authentication-related API calls.
//   This controller is intentionally THIN — it only:
//     1. Maps HTTP routes to methods
//     2. Receives request parameters from the frontend
//     3. Delegates ALL business logic to AuthService
//     4. Returns the response (JSON map) from the service
//
// BASE URL:  /api/auth
//
// ENDPOINTS:
//   POST /api/auth/login          → Log in with email + password
//   POST /api/auth/register       → Create a new account
//   POST /api/auth/reset-password → Change password for existing account
//
// CALLED BY:
//   Index.html       (login form)
//   Register.html    (registration form)
//   ResetPassword.html (password reset form)
// ============================================================

import com.ddas.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController                    // Returns JSON responses (not HTML views)
@RequestMapping("/api/auth")       // All routes in this class are prefixed with /api/auth
@CrossOrigin                       // Allow CORS from any origin (HTML frontend)
public class AuthController {

    // ── Dependency ───────────────────────────────────────────────
    // AuthService handles all business logic for authentication
    @Autowired
    private AuthService authService;
    // ────────────────────────────────────────────────────────────


    /**
     * POST /api/auth/login
     *
     * Authenticates a user with their email and password.
     * Delegates to AuthService.login() for actual verification.
     *
     * Request Params (form data):
     *   email    - The user's email address
     *   password - The user's plain-text password
     *
     * Response JSON:
     *   { "status": "success", "message": "...", "email": "..." }
     *   { "status": "error",   "message": "..." }
     */
    @PostMapping("/login")
    public Map<String, String> login(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        return authService.login(email, password);
    }


    /**
     * POST /api/auth/register
     *
     * Creates a new user account with email and password.
     * Delegates to AuthService.register() for validation & saving.
     *
     * Request Params (form data):
     *   email    - Desired email (must be unique)
     *   password - Desired password (min 6 chars)
     *
     * Response JSON:
     *   { "status": "success", "message": "Account created! You can now log in." }
     *   { "status": "error",   "message": "..." }
     */
    @PostMapping("/register")
    public Map<String, String> register(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        return authService.register(email, password);
    }


    /**
     * POST /api/auth/reset-password
     *
     * Resets the password for an existing account.
     * Delegates to AuthService.resetPassword() for validation & update.
     *
     * Request Params (form data):
     *   email       - The registered email of the account to update
     *   newPassword - The new password to set (min 6 chars)
     *
     * Response JSON:
     *   { "status": "success", "message": "Password reset successfully! ..." }
     *   { "status": "error",   "message": "..." }
     */
    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(
            @RequestParam("email") String email,
            @RequestParam("newPassword") String newPassword) {

        return authService.resetPassword(email, newPassword);
    }
}
