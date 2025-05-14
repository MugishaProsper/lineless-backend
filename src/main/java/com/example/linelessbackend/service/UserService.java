package com.example.linelessbackend.service;

import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.Queue;
import com.example.linelessbackend.model.Role;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.repository.CompanyRepository;
import com.example.linelessbackend.repository.QueueRepository;
import com.example.linelessbackend.repository.TokenRepository;
import com.example.linelessbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final QueueRepository queueRepository;
    private final TokenRepository tokenRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public UserService(UserRepository userRepository, QueueRepository queueRepository, 
                      TokenRepository tokenRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.queueRepository = queueRepository;
        this.tokenRepository = tokenRepository;
        this.companyRepository = companyRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User updateUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.valueOf(role));
        return userRepository.save(user);
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.countByRole(Role.USER));
        stats.put("totalAdmins", userRepository.countByRole(Role.ADMIN));
        stats.put("totalSuperAdmins", userRepository.countByRole(Role.SUPER_ADMIN));
        stats.put("totalQueues", queueRepository.count());
        stats.put("totalTokens", tokenRepository.count());
        return stats;
    }
    
    public Map<String, Object> getAdminStats(Long adminId) {
        Map<String, Object> stats = new HashMap<>();
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        // Get companies managed by the admin
        List<Company> companies = companyRepository.findByAdminsContaining(admin);
        stats.put("totalCompanies", companies.size());
        
        // Get queues directly managed by admin
        List<Queue> directQueues = queueRepository.findByAdminId(adminId);
        stats.put("directQueues", directQueues.size());
        
        // Get all queues from admin's companies
        int totalQueues = directQueues.size();
        for (Company company : companies) {
            List<Queue> companyQueues = queueRepository.findByCompanyId(company.getId());
            totalQueues += companyQueues.size();
        }
        stats.put("totalQueues", totalQueues);
        
        // Get active tokens for admin's queues
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        
        long activeTokens = 0;
        long todayTokens = 0;
        
        // Count for direct queues
        for (Queue queue : directQueues) {
            activeTokens += tokenRepository.countByQueueIdAndStatus(queue.getId(), Token.Status.ACTIVE);
            todayTokens += tokenRepository.countByQueueIdAndCreatedAtBetween(queue.getId(), startOfDay, endOfDay);
        }
        
        // Count for company queues
        for (Company company : companies) {
            List<Queue> companyQueues = queueRepository.findByCompanyId(company.getId());
            for (Queue queue : companyQueues) {
                if (!directQueues.contains(queue)) { // Avoid double counting
                    activeTokens += tokenRepository.countByQueueIdAndStatus(queue.getId(), Token.Status.ACTIVE);
                    todayTokens += tokenRepository.countByQueueIdAndCreatedAtBetween(queue.getId(), startOfDay, endOfDay);
                }
            }
        }
        
        stats.put("activeTokens", activeTokens);
        stats.put("todayTokens", todayTokens);
        
        return stats;
    }

    public Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return user.getId();
    }

    public User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}