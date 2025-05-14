package com.example.linelessbackend.model;

public enum Role {
    USER,           // Normal user who can view queues and request tokens
    ADMIN,          // Company representative who can create and manage queues
    SUPER_ADMIN     // Application moderator who can oversee everything
} 