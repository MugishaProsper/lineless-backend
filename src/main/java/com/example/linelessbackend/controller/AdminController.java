package com.example.linelessbackend.controller;

import com.example.linelessbackend.dto.AdminStatsDTO;
import com.example.linelessbackend.dto.CompanyDTO;
import com.example.linelessbackend.dto.QueueDTO;
import com.example.linelessbackend.dto.TokenDTO;
import com.example.linelessbackend.dto.UserDTO;
import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.service.CompanyService;
import com.example.linelessbackend.service.QueueService;
import com.example.linelessbackend.service.TokenService;
import com.example.linelessbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminController {
    private final QueueService queueService;
    private final TokenService tokenService;
    private final UserService userService;
    private final CompanyService companyService;

    public AdminController(QueueService queueService, TokenService tokenService, 
                           UserService userService, CompanyService companyService) {
        this.queueService = queueService;
        this.tokenService = tokenService;
        this.userService = userService;
        this.companyService = companyService;
    }

    // Get the authenticated admin's companies
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAdminCompanies(Authentication authentication) {
        String email = authentication.getName();
        UserDTO userDTO = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        List<CompanyDTO> companies = companyService.getCompaniesByAdmin(userDTO.getId());
        return ResponseEntity.ok(companies);
    }

    // Get queues for the admin's companies
    @GetMapping("/queues")
    public ResponseEntity<List<QueueDTO>> getCompanyQueues(Authentication authentication) {
        String email = authentication.getName();
        UserDTO userDTO = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        List<QueueDTO> queues = queueService.getQueuesByAdmin(userDTO.getId());
        return ResponseEntity.ok(queues);
    }
    
    // Create a new queue for a company
    @PostMapping("/companies/{companyId}/queues")
    public ResponseEntity<QueueDTO> createQueue(
            @PathVariable Long companyId, 
            @RequestBody QueueDTO queueDTO,
            Authentication authentication) {
        String email = authentication.getName();
        UserDTO userDTO = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Validate that the admin belongs to the company
        List<CompanyDTO> adminCompanies = companyService.getCompaniesByAdmin(userDTO.getId());
        boolean hasAccess = adminCompanies.stream()
                .anyMatch(company -> company.getId().equals(companyId));
        
        if (!hasAccess) {
            return ResponseEntity.status(403).build();
        }
        
        queueDTO.setCompanyId(companyId);
        QueueDTO createdQueue = queueService.createQueue(queueDTO, userDTO.getId());
        return ResponseEntity.ok(createdQueue);
    }
    
    // Update a queue
    @PutMapping("/queues/{queueId}")
    public ResponseEntity<QueueDTO> updateQueue(
            @PathVariable Long queueId, 
            @RequestBody QueueDTO queueDTO,
            Authentication authentication) {
        String email = authentication.getName();
        UserDTO userDTO = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Validate the admin has access to this queue
        boolean hasAccess = queueService.isQueueOwnedByAdmin(queueId, userDTO.getId());
        if (!hasAccess) {
            return ResponseEntity.status(403).build();
        }
        
        QueueDTO updatedQueue = queueService.updateQueue(queueId, queueDTO);
        return ResponseEntity.ok(updatedQueue);
    }
    
    // Delete a queue
    @DeleteMapping("/queues/{queueId}")
    public ResponseEntity<Void> deleteQueue(
            @PathVariable Long queueId,
            Authentication authentication) {
        String email = authentication.getName();
        UserDTO userDTO = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Validate the admin has access to this queue
        boolean hasAccess = queueService.isQueueOwnedByAdmin(queueId, userDTO.getId());
        if (!hasAccess) {
            return ResponseEntity.status(403).build();
        }
        
        queueService.deleteQueue(queueId);
        return ResponseEntity.ok().build();
    }
    
    // Get tokens for a queue
    @GetMapping("/queues/{queueId}/tokens")
    public ResponseEntity<List<TokenDTO>> getQueueTokens(
            @PathVariable Long queueId,
            Authentication authentication) {
        String email = authentication.getName();
        UserDTO userDTO = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        // Validate the admin has access to this queue
        boolean hasAccess = queueService.isQueueOwnedByAdmin(queueId, userDTO.getId());
        if (!hasAccess) {
            return ResponseEntity.status(403).build();
        }
        
        List<TokenDTO> tokens = tokenService.getTokensByQueue(queueId);
        return ResponseEntity.ok(tokens);
    }
    
    // Update token status (call next token, complete token, etc.)
    @PutMapping("/tokens/{tokenId}/status")
    public ResponseEntity<TokenDTO> updateTokenStatus(
            @PathVariable Long tokenId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String email = authentication.getName();
        UserDTO userDTO = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        String status = request.get("status");
        Token.Status newStatus = Token.Status.valueOf(status.toUpperCase());
        
        // Validate the admin has access to this token
        boolean hasAccess = tokenService.isTokenQueueOwnedByAdmin(tokenId, userDTO.getId());
        if (!hasAccess) {
            return ResponseEntity.status(403).build();
        }
        
        TokenDTO updatedToken = tokenService.updateTokenStatus(tokenId, newStatus);
        return ResponseEntity.ok(updatedToken);
    }
    
    // Get dashboard stats for the admin
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getAdminStats(Authentication authentication) {
        String email = authentication.getName();
        UserDTO userDTO = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        AdminStatsDTO stats = userService.getAdminStats(userDTO.getId());
        return ResponseEntity.ok(stats);
    }
}