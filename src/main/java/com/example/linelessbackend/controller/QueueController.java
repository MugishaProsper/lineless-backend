package com.example.linelessbackend.controller;

import com.example.linelessbackend.dto.QueueDTO;
import com.example.linelessbackend.dto.TokenDTO;
import com.example.linelessbackend.model.Queue;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.service.QueueService;
import com.example.linelessbackend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class QueueController {

    private static final Logger logger = LoggerFactory.getLogger(QueueController.class);

    @Autowired
    private QueueService queueService;

    @Autowired
    private UserService userService;

    // User endpoints
    @GetMapping("/queues")
    public ResponseEntity<List<QueueDTO>> getAvailableQueues() {
        return ResponseEntity.ok(queueService.getAvailableQueues());
    }

    // Super Admin endpoints
    @GetMapping("/super-admin/queues")
    public ResponseEntity<List<QueueDTO>> getAllQueues() {
        return ResponseEntity.ok(queueService.getAllQueues());
    }

    @PostMapping("/super-admin/queues")
    public ResponseEntity<QueueDTO> createQueueWithAdmin(@RequestBody QueueDTO queueDTO) {
        return ResponseEntity.ok(queueService.createQueueWithAdmin(queueDTO));
    }

    @PutMapping("/super-admin/queues/{id}/admin")
    public ResponseEntity<QueueDTO> assignAdminToQueue(@PathVariable Long id, @RequestBody Long adminId) {
        return ResponseEntity.ok(queueService.assignAdminToQueue(id, adminId));
    }

    @DeleteMapping("/super-admin/queues/{id}")
    public ResponseEntity<Void> deleteQueueAsSuperAdmin(@PathVariable Long id) {
        queueService.deleteQueueAsSuperAdmin(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/queues/{queueId}/tokens")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<TokenDTO> issueToken(@PathVariable Long queueId, Authentication authentication) {
        String email = authentication.getName();
        logger.info("Issuing token for user with email: {}", email);
        User user = userService.getUserByEmail(email);
        logger.info("Found user with ID: {}", user.getId());
        Token token = queueService.issueToken(queueId, user);
        logger.info("Created token with ID: {}", token.getId());
        return ResponseEntity.ok(TokenDTO.fromToken(token));
    }

    @PostMapping("/queues/{queueId}/serve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<TokenDTO> serveNextToken(@PathVariable Long queueId) {
        Token token = queueService.serveNextToken(queueId);
        return ResponseEntity.ok(TokenDTO.fromToken(token));
    }

    @GetMapping("/queues/{queueId}/tokens")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<TokenDTO>> getQueueTokens(@PathVariable Long queueId) {
        List<Token> tokens = queueService.getQueueTokens(queueId);
        List<TokenDTO> tokenDTOs = tokens.stream()
            .map(TokenDTO::fromToken)
            .collect(Collectors.toList());
        return ResponseEntity.ok(tokenDTOs);
    }

    @GetMapping("/tokens/my-tokens")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<TokenDTO>> getUserTokens(Authentication authentication) {
        String email = authentication.getName();
        logger.info("Getting tokens for user with email: {}", email);
        User user = userService.getUserByEmail(email);
        logger.info("Found user with ID: {}", user.getId());
        List<Token> tokens = queueService.getUserTokens(user.getId());
        logger.info("Found {} tokens for user", tokens.size());
        List<TokenDTO> tokenDTOs = tokens.stream()
            .map(TokenDTO::fromToken)
            .collect(Collectors.toList());
        return ResponseEntity.ok(tokenDTOs);
    }

    @PostMapping("/tokens/{tokenId}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> cancelToken(@PathVariable Long tokenId) {
        queueService.cancelToken(tokenId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tokens/{tokenId}/skip")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> skipToken(@PathVariable Long tokenId) {
        queueService.skipToken(tokenId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/queues/{queueId}/close")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> closeQueue(@PathVariable Long queueId) {
        queueService.closeQueue(queueId);
        return ResponseEntity.ok().build();
    }
} 