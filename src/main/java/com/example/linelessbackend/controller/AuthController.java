package com.example.linelessbackend.controller;

import com.example.linelessbackend.model.Role;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        logger.info("Processing registration request for user: {}", email);
        
        Role role = Role.valueOf(request.getOrDefault("role", "USER"));
        
        User user = authService.registerUser(name, email, request.get("password"), role);
        logger.info("User registered successfully: {}", email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        logger.info("Processing login request for user: {}", email);
        
        Map<String, Object> response = authService.loginUser(email, request.get("password"));
        logger.info("User logged in successfully: {}", email);
        return ResponseEntity.ok(response);
    }
}