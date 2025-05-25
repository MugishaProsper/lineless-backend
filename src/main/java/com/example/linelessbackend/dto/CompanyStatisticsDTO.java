package com.example.linelessbackend.dto;

import java.time.LocalDateTime;

public class CompanyStatisticsDTO {
    private Long companyId;
    private String companyName;
    private int totalQueues;
    private int activeQueues;
    private int totalTokens;
    private int activeTokens;
    private int completedTokens;
    private int cancelledTokens;
    private double averageWaitTime;
    private LocalDateTime lastUpdated;

    // Getters
    public Long getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public int getTotalQueues() { return totalQueues; }
    public int getActiveQueues() { return activeQueues; }
    public int getTotalTokens() { return totalTokens; }
    public int getActiveTokens() { return activeTokens; }
    public int getCompletedTokens() { return completedTokens; }
    public int getCancelledTokens() { return cancelledTokens; }
    public double getAverageWaitTime() { return averageWaitTime; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }

    // Setters
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setTotalQueues(int totalQueues) { this.totalQueues = totalQueues; }
    public void setActiveQueues(int activeQueues) { this.activeQueues = activeQueues; }
    public void setTotalTokens(int totalTokens) { this.totalTokens = totalTokens; }
    public void setActiveTokens(int activeTokens) { this.activeTokens = activeTokens; }
    public void setCompletedTokens(int completedTokens) { this.completedTokens = completedTokens; }
    public void setCancelledTokens(int cancelledTokens) { this.cancelledTokens = cancelledTokens; }
    public void setAverageWaitTime(double averageWaitTime) { this.averageWaitTime = averageWaitTime; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
} 