package com.ddas.backend.controller;

import com.ddas.backend.model.User;
import com.ddas.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        Map<String, String> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("email", userOpt.get().getEmail());
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

        if (userRepository.existsByEmail(email)) {
            response.put("status", "error");
            response.put("message", "An account with this email already exists");
            return response;
        }

        User user = new User(email, passwordEncoder.encode(password));
        userRepository.save(user);
        response.put("status", "success");
        response.put("message", "Account created! You can now log in.");
        return response;
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(
            @RequestParam("email") String email,
            @RequestParam("newPassword") String newPassword) {

        Map<String, String> response = new HashMap<>();

        if (email == null || email.isBlank() || newPassword == null || newPassword.length() < 6) {
            response.put("status", "error");
            response.put("message", "Email is required and password must be at least 6 characters");
            return response;
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No account found with that email address");
            return response;
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        response.put("status", "success");
        response.put("message", "Password reset successfully! You can now log in.");
        return response;
    }
}
