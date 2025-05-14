package com.example.linelessbackend.service;

import com.example.linelessbackend.dto.TokenDTO;
import com.example.linelessbackend.model.Queue;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.repository.QueueRepository;
import com.example.linelessbackend.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final QueueRepository queueRepository;
    private final QueueService queueService;

    @Autowired
    public TokenService(TokenRepository tokenRepository, QueueRepository queueRepository, QueueService queueService) {
        this.tokenRepository = tokenRepository;
        this.queueRepository = queueRepository;
        this.queueService = queueService;
    }

    public List<Token> getAdminTokens(Long adminId) {
        return tokenRepository.findByQueueAdminId(adminId);
    }
    
    public List<TokenDTO> getTokensByQueue(Long queueId) {
        List<Token> tokens = tokenRepository.findByQueueId(queueId);
        
        return tokens.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public boolean isTokenQueueOwnedByAdmin(Long tokenId, Long adminId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));
        
        return queueService.isQueueOwnedByAdmin(token.getQueue().getId(), adminId);
    }

    @Transactional
    public Token approveToken(Long tokenId, Long adminId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!token.getQueue().getAdmin().getId().equals(adminId)) {
            throw new RuntimeException("Unauthorized to approve this token");
        }

        if (token.getStatus() != Token.Status.WAITING) {
            throw new RuntimeException("Token is not in waiting status");
        }

        token.setApprovedAt(LocalDateTime.now());
        token.setPosition((int) (tokenRepository.countByQueueId(token.getQueue().getId()) + 1));
        token.setEstimatedWaitTime(calculateEstimatedWaitTime(token.getQueue().getId()));

        return tokenRepository.save(token);
    }

    @Transactional
    public Token rejectToken(Long tokenId, Long adminId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!token.getQueue().getAdmin().getId().equals(adminId)) {
            throw new RuntimeException("Unauthorized to reject this token");
        }

        if (token.getStatus() != Token.Status.WAITING) {
            throw new RuntimeException("Token is not in waiting status");
        }

        token.setStatus(Token.Status.CANCELLED);
        return tokenRepository.save(token);
    }

    @Transactional
    public TokenDTO updateTokenStatus(Long tokenId, Token.Status newStatus) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        token.setStatus(newStatus);
        
        switch (newStatus) {
            case ACTIVE:
                token.setStartedAt(LocalDateTime.now());
                break;
            case COMPLETED:
                token.setCompletedAt(LocalDateTime.now());
                // Update estimated wait time for other tokens
                updateQueueWaitTimes(token.getQueue().getId());
                break;
            case CANCELLED:
            case SKIPPED:
                // Update estimated wait time for other tokens
                updateQueueWaitTimes(token.getQueue().getId());
                break;
        }

        return convertToDTO(tokenRepository.save(token));
    }

    private Integer calculateEstimatedWaitTime(Long queueId) {
        List<Token> completedTokens = tokenRepository.findByQueueIdAndStatus(queueId, Token.Status.COMPLETED);
        
        if (completedTokens.isEmpty()) {
            return 15; // Default 15 minutes if no history
        }

        double averageServiceTime = completedTokens.stream()
            .filter(token -> token.getStartedAt() != null && token.getCompletedAt() != null)
            .mapToLong(token -> Duration.between(token.getStartedAt(), token.getCompletedAt()).toMinutes())
            .average()
            .orElse(15.0);

        // Calculate position-based wait time
        long waitingTokens = tokenRepository.countByQueueIdAndStatus(queueId, Token.Status.WAITING);
        return (int) (averageServiceTime * waitingTokens);
    }

    private void updateQueueWaitTimes(Long queueId) {
        List<Token> waitingTokens = tokenRepository.findByQueueIdAndStatus(queueId, Token.Status.WAITING);
        Integer newWaitTime = calculateEstimatedWaitTime(queueId);
        
        for (Token token : waitingTokens) {
            token.setEstimatedWaitTime(newWaitTime);
            tokenRepository.save(token);
        }
    }
    
    private TokenDTO convertToDTO(Token token) {
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