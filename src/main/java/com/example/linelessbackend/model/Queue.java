package com.example.linelessbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "queues")
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private int estimatedTimePerToken; // in minutes

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public void setServiceName(String serviceName) { this.serviceName = serviceName; };
    public String getServiceName(String serviceName) { return this.serviceName ; };
    public boolean isActive() { return isActive; };
    public void setIsActive(boolean isActive) { this.isActive = isActive; };
    public int getEstimatedTimePerToken() { return estimatedTimePerToken; };
    public void setEstimatedTimePerToken(int estimatedTimePerToken){ this.estimatedTimePerToken = estimatedTimePerToken;};
    public LocalDateTime getCreatedAt() { return createdAt; };
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; };


    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL)
    private List<Token> tokens;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 