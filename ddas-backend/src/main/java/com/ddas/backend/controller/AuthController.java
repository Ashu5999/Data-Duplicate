package com.ddas.backend.controller;

import com.ddas.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private UserRepository userRepository = new UserRepository();

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        Map<String, String> response = new HashMap<>();

        if (userRepository.validate(email, password)) {
            response.put("status", "success");
            response.put("message", "Login successful");
        } else {
            response.put("status", "error");
            response.put("message", "Invalid email or password");
        }

        return response;
    }

    @PostMapping("/register")
    public Map<String, String> register(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        Map<String, String> response = new HashMap<>();

        if (email == null || email.isBlank() || password == null || password.length() < 6) {
            response.put("status", "error");
            response.put("message", "Email is required and password must be at least 6 characters");
            return response;
        }

        if (userRepository.emailExists(email)) {
            response.put("status", "error");
            response.put("message", "An account with this email already exists");
            return response;
        }

        userRepository.register(email, password);
        response.put("status", "success");
        response.put("message", "Account created! You can now log in.");
        return response;
    }
}
