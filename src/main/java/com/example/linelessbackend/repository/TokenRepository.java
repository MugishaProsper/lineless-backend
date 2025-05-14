package com.example.linelessbackend.repository;

import com.example.linelessbackend.model.Queue;
import com.example.linelessbackend.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT t FROM Token t JOIN FETCH t.queue WHERE t.user.id = :userId")
    List<Token> findByUserId(Long userId);
    
    List<Token> findByQueueId(Long queueId);
    List<Token> findByQueueIdAndStatusOrderByPositionAsc(Long queueId, Token.Status status);
    List<Token> findByQueueAdminId(Long adminId);
    List<Token> findByQueueIdAndStatus(Long queueId, Token.Status status);
    Long countByQueueInAndStatus(List<Queue> queues, Token.Status status);
    Long countByQueueInAndCreatedAtBetween(List<Queue> queues, LocalDateTime start, LocalDateTime end);
    Long countByStatus(Token.Status status);
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Long countByQueueId(Long queueId);
    Long countByQueueIdAndStatus(Long queueId, Token.Status status);
    Long countByQueueIdAndCreatedAtBetween(Long queueId, LocalDateTime start, LocalDateTime end);
    Long countByUserIdAndStatus(Long userId, Token.Status status);
    Long countByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
    List<Token> findByQueueIdAndUserId(Long queueId, Long userId);
} 