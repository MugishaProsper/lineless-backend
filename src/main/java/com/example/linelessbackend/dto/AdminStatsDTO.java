package com.example.linelessbackend.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class AdminStatsDTO {
    private Long adminId;
    private String adminName;
    private String adminEmail;
    private LocalDateTime lastLogin;
    private Map<String, Object> statistics;

    public AdminStatsDTO() {
        this.statistics = new HashMap<>();
    }

    // Getters and Setters
    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Map<String, Object> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<String, Object> statistics) {
        this.statistics = statistics;
    }
} 