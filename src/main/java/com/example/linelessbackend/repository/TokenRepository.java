package com.example.linelessbackend.repository;

import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.model.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByQueueIdAndStatusOrderByPositionAsc(Long queueId, TokenStatus status);
    List<Token> findByUserId(Long userId);
    List<Token> findByQueueId(Long queueId);
    int countByQueueIdAndStatus(Long queueId, TokenStatus status);
} 