package com.example.linelessbackend.service;

import com.example.linelessbackend.model.*;
import com.example.linelessbackend.repository.QueueRepository;
import com.example.linelessbackend.repository.TokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueueService {
    private final QueueRepository queueRepository;
    private final TokenRepository tokenRepository;

    public QueueService(QueueRepository queueRepository, TokenRepository tokenRepository) {
        this.queueRepository = queueRepository;
        this.tokenRepository = tokenRepository;
    }

    public Queue createQueue(String serviceName, int estimatedTimePerToken) {
        Queue queue = new Queue();
        queue.setServiceName(serviceName);
        queue.setEstimatedTimePerToken(estimatedTimePerToken);
        queue.setIsActive(true);
        return queueRepository.save(queue);
    }

    public List<Queue> getActiveQueues() {
        return queueRepository.findByIsActive(true);
    }

    public Token issueToken(Long queueId, User user) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        if (!queue.isActive()) {
            throw new RuntimeException("Queue is not active");
        }

        int nextPosition = tokenRepository.countByQueueIdAndStatus(queueId, TokenStatus.WAITING) + 1;

        Token token = new Token();
        token.setQueue(queue);
        token.setUser(user);
        token.setPosition(nextPosition);
        token.setStatus(TokenStatus.WAITING);

        return tokenRepository.save(token);
    }

    @Transactional
    public Token serveNextToken(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        Token currentToken = tokenRepository.findByQueueIdAndStatusOrderByPositionAsc(queueId, TokenStatus.SERVING)
                .stream()
                .findFirst()
                .orElse(null);

        if (currentToken != null) {
            currentToken.setStatus(TokenStatus.COMPLETED);
            currentToken.setServedAt(LocalDateTime.now());
            tokenRepository.save(currentToken);
        }

        Token nextToken = tokenRepository.findByQueueIdAndStatusOrderByPositionAsc(queueId, TokenStatus.WAITING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No waiting tokens in queue"));

        nextToken.setStatus(TokenStatus.SERVING);
        return tokenRepository.save(nextToken);
    }

    public List<Token> getQueueTokens(Long queueId) {
        return tokenRepository.findByQueueId(queueId);
    }

    public List<Token> getUserTokens(Long userId) {
        return tokenRepository.findByUserId(userId);
    }

    public void cancelToken(Long tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (token.getStatus() != TokenStatus.WAITING) {
            throw new RuntimeException("Can only cancel waiting tokens");
        }

        token.setStatus(TokenStatus.CANCELLED);
        tokenRepository.save(token);
    }

    public void skipToken(Long tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (token.getStatus() != TokenStatus.WAITING) {
            throw new RuntimeException("Can only skip waiting tokens");
        }

        token.setStatus(TokenStatus.SKIPPED);
        tokenRepository.save(token);
    }

    public void closeQueue(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new RuntimeException("Queue not found"));

        queue.setIsActive(false);
        queueRepository.save(queue);
    }
} 