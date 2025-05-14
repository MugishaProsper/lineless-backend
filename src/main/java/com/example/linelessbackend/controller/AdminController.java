package com.example.linelessbackend.controller;

import com.example.linelessbackend.dto.QueueDTO;
import com.example.linelessbackend.dto.TokenDTO;
import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.service.CompanyService;
import com.example.linelessbackend.service.QueueService;
import com.example.linelessbackend.service.TokenService;
import com.example.linelessbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<Company>> getMyCompanies(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Company> companies = companyService.getCompaniesByAdmin(user.getId());
        return ResponseEntity.ok(companies);
    }

    // Get queues for the admin's companies
    @GetMapping("/queues")
    public ResponseEntity<List<QueueDTO>> getCompanyQueues(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<QueueDTO> queues = queueService.getQueuesByAdmin(user.getId());
        return ResponseEntity.ok(queues);
    }
    
    // Create a new queue for a company
    @PostMapping("/companies/{companyId}/queues")
    public ResponseEntity<QueueDTO> createQueue(
            @PathVariable Long companyId, 
            @RequestBody QueueDTO queueDTO,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate that the admin belongs to the company
        List<Company> adminCompanies = companyService.getCompaniesByAdmin(user.getId());
        boolean hasAccess = adminCompanies.stream()
                .anyMatch(company -> company.getId().equals(companyId));
        
        if (!hasAccess) {
            return ResponseEntity.status(403).build();
        }
        
        queueDTO.setCompanyId(companyId);
        QueueDTO createdQueue = queueService.createQueue(queueDTO, user.getId());
        return ResponseEntity.ok(createdQueue);
    }
    
    // Update a queue
    @PutMapping("/queues/{queueId}")
    public ResponseEntity<QueueDTO> updateQueue(
            @PathVariable Long queueId, 
            @RequestBody QueueDTO queueDTO,
            Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate the admin has access to this queue
        boolean hasAccess = queueService.isQueueOwnedByAdmin(queueId, user.getId());
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
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate the admin has access to this queue
        boolean hasAccess = queueService.isQueueOwnedByAdmin(queueId, user.getId());
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
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate the admin has access to this queue
        boolean hasAccess = queueService.isQueueOwnedByAdmin(queueId, user.getId());
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
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String status = request.get("status");
        Token.Status newStatus = Token.Status.valueOf(status.toUpperCase());
        
        // Validate the admin has access to this token
        boolean hasAccess = tokenService.isTokenQueueOwnedByAdmin(tokenId, user.getId());
        if (!hasAccess) {
            return ResponseEntity.status(403).build();
        }
        
        TokenDTO updatedToken = tokenService.updateTokenStatus(tokenId, newStatus);
        return ResponseEntity.ok(updatedToken);
    }
    
    // Get dashboard stats for the admin
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> stats = userService.getAdminStats(user.getId());
        return ResponseEntity.ok(stats);
    }
    
    // Alias for dashboard to support the stats endpoint used by the frontend
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(Authentication authentication) {
        return getDashboardStats(authentication);
    }
}