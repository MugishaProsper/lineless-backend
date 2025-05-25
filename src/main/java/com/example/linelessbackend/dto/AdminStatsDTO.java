package com.example.linelessbackend.dto;

public class AdminStatsDTO {
    private Long adminId;
    private int totalCompanies;
    private int totalQueues;
    private int totalTokens;

    // Getters and Setters
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public int getTotalCompanies() { return totalCompanies; }
    public void setTotalCompanies(int totalCompanies) { this.totalCompanies = totalCompanies; }

    public int getTotalQueues() { return totalQueues; }
    public void setTotalQueues(int totalQueues) { this.totalQueues = totalQueues; }

    public int getTotalTokens() { return totalTokens; }
    public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
} 