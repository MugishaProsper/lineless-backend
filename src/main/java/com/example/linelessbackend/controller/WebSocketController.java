package com.example.linelessbackend.controller;

import com.example.linelessbackend.model.Queue;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.service.QueueService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private final QueueService queueService;

    public WebSocketController(QueueService queueService) {
        this.queueService = queueService;
    }

    @MessageMapping("/queue/{queueId}/subscribe")
    @SendTo("/topic/queue/{queueId}")
    public Queue subscribeToQueue(Long queueId) {
        return queueService.getQueueTokens(queueId).get(0).getQueue();
    }

    @MessageMapping("/queue/{queueId}/token-update")
    @SendTo("/topic/queue/{queueId}/tokens")
    public Token updateTokenStatus(Long queueId, Token token) {
        return token;
    }
} 