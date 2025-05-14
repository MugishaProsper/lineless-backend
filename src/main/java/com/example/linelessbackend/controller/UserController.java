package com.example.linelessbackend.controller;

import com.example.linelessbackend.dto.StatisticsDTO;
import com.example.linelessbackend.service.StatisticsService;
import com.example.linelessbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    @GetMapping("/stats")
    public ResponseEntity<StatisticsDTO> getUserStatistics(Authentication authentication) {
        Long userId = userService.getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(statisticsService.getUserStatistics(userId));
    }
} 