package com.example.linelessbackend.dto;

import com.example.linelessbackend.model.Token;
import java.time.LocalDateTime;

public class TokenDTO {
    private Long id;
    private Long queueId;
    private String queueName;
    private Integer position;
    private Token.Status status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer estimatedWaitTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public TokenDTO() {}
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getQueueId() {
        return queueId;
    }
    
    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }
    
    public String getQueueName() {
        return queueName;
    }
    
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
    
    public Integer getPosition() {
        return position;
    }
    
    public void setPosition(Integer position) {
        this.position = position;
    }
    
    public Token.Status getStatus() {
        return status;
    }
    
    public void setStatus(Token.Status status) {
        this.status = status;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
    
    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public Integer getEstimatedWaitTime() {
        return estimatedWaitTime;
    }
    
    public void setEstimatedWaitTime(Integer estimatedWaitTime) {
        this.estimatedWaitTime = estimatedWaitTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public static TokenDTO fromToken(Token token) {
        TokenDTO dto = new TokenDTO();
        dto.setId(token.getId());
        dto.setQueueId(token.getQueue().getId());
        dto.setQueueName(token.getQueue().getName());
        dto.setPosition(token.getPosition());
        dto.setStatus(token.getStatus());
        dto.setRequestedAt(token.getRequestedAt());
        dto.setApprovedAt(token.getApprovedAt());
        dto.setStartedAt(token.getStartedAt());
        dto.setCompletedAt(token.getCompletedAt());
        dto.setEstimatedWaitTime(token.getEstimatedWaitTime());
        dto.setCreatedAt(token.getCreatedAt());
        dto.setUpdatedAt(token.getUpdatedAt());
        return dto;
    }
} 