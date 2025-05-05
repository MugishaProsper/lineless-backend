package com.example.linelessbackend.controller;

import com.example.linelessbackend.model.Queue;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.service.QueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queues")
public class QueueController {
    private final QueueService queueService;

    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @PostMapping
    public ResponseEntity<Queue> createQueue(@RequestBody Map<String, Object> request) {
        String serviceName = (String) request.get("serviceName");
        int estimatedTimePerToken = (int) request.get("estimatedTimePerToken");
        Queue queue = queueService.createQueue(serviceName, estimatedTimePerToken);
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Queue>> getActiveQueues() {
        List<Queue> queues = queueService.getActiveQueues();
        return ResponseEntity.ok(queues);
    }

    @PostMapping("/{queueId}/tokens")
    public ResponseEntity<Token> issueToken(@PathVariable Long queueId, @AuthenticationPrincipal User user) {
        Token token = queueService.issueToken(queueId, user);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/{queueId}/serve")
    public ResponseEntity<Token> serveNextToken(@PathVariable Long queueId) {
        Token token = queueService.serveNextToken(queueId);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/{queueId}/tokens")
    public ResponseEntity<List<Token>> getQueueTokens(@PathVariable Long queueId) {
        List<Token> tokens = queueService.getQueueTokens(queueId);
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/my-tokens")
    public ResponseEntity<List<Token>> getUserTokens(@AuthenticationPrincipal User user) {
        List<Token> tokens = queueService.getUserTokens(user.getId());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/tokens/{tokenId}/cancel")
    public ResponseEntity<Void> cancelToken(@PathVariable Long tokenId) {
        queueService.cancelToken(tokenId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tokens/{tokenId}/skip")
    public ResponseEntity<Void> skipToken(@PathVariable Long tokenId) {
        queueService.skipToken(tokenId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{queueId}/close")
    public ResponseEntity<Void> closeQueue(@PathVariable Long queueId) {
        queueService.closeQueue(queueId);
        return ResponseEntity.ok().build();
    }
} 