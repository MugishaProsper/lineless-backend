package com.example.linelessbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column
    private LocalDateTime servedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenStatus status;

    public Long getId() { return id; };
    public void setId(Long id) { this.id = id; };
    public Queue getQueue() { return queue; };
    public void setQueue(Queue queue) { this.queue = queue; };
    public User getUser() { return user; };
    public void setUser(User user) { this.user = user; };
    public int getPosition() { return position; };
    public void setPosition(int position) { this.position = position; };
    public LocalDateTime getIssuedAt() { return issuedAt; };
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; };
    public LocalDateTime getServedAt() { return servedAt; };
    public void setServedAt(LocalDateTime servedAt) { this.servedAt = servedAt; };
    public TokenStatus getStatus() { return status; };
    public void setStatus(TokenStatus status) { this.status = status; };


    @PrePersist
    protected void onCreate() {
        issuedAt = LocalDateTime.now();
        status = TokenStatus.WAITING;
    }
} 