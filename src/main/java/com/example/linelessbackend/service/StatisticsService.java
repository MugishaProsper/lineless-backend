package com.example.linelessbackend.service;

import com.example.linelessbackend.dto.StatisticsDTO;
import com.example.linelessbackend.model.Queue;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.model.Role;
import com.example.linelessbackend.repository.QueueRepository;
import com.example.linelessbackend.repository.TokenRepository;
import com.example.linelessbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "generalStats", unless = "#result == null")
    public Map<String, Object> getStatistics() {
        // Get today's date range in UTC
        LocalDateTime startOfDay = LocalDateTime.now(ZoneId.of("UTC")).with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now(ZoneId.of("UTC")).with(LocalTime.MAX);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQueues", queueRepository.count());
        stats.put("activeTokens", tokenRepository.countByStatus(Token.Status.ACTIVE));
        stats.put("todayTokens", tokenRepository.countByCreatedAtBetween(startOfDay, endOfDay));
        stats.put("totalUsers", userRepository.countByRole(Role.USER));
        stats.put("totalAdmins", userRepository.countByRole(Role.ADMIN));
        stats.put("totalSuperAdmins", userRepository.countByRole(Role.SUPER_ADMIN));
        
        return stats;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "adminStats", key = "#adminId", unless = "#result == null")
    public StatisticsDTO getAdminStatistics(Long adminId) {
        // Get queues managed by this admin
        List<Queue> managedQueues = queueRepository.findByAdminId(adminId);
        
        // Get today's date range in UTC
        LocalDateTime startOfDay = LocalDateTime.now(ZoneId.of("UTC")).with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now(ZoneId.of("UTC")).with(LocalTime.MAX);

        return StatisticsDTO.builder()
                .totalQueues((long) managedQueues.size())
                .activeTokens(tokenRepository.countByQueueInAndStatus(managedQueues, Token.Status.ACTIVE))
                .todayTokens(tokenRepository.countByQueueInAndCreatedAtBetween(managedQueues, startOfDay, endOfDay))
                .managedQueues((long) managedQueues.size())
                .managedActiveTokens(tokenRepository.countByQueueInAndStatus(managedQueues, Token.Status.ACTIVE))
                .managedTodayTokens(tokenRepository.countByQueueInAndCreatedAtBetween(managedQueues, startOfDay, endOfDay))
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "superAdminStats", unless = "#result == null")
    public StatisticsDTO getSuperAdminStatistics() {
        // Get today's date range in UTC
        LocalDateTime startOfDay = LocalDateTime.now(ZoneId.of("UTC")).with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now(ZoneId.of("UTC")).with(LocalTime.MAX);

        return StatisticsDTO.builder()
                .totalQueues(queueRepository.count())
                .activeTokens(tokenRepository.countByStatus(Token.Status.ACTIVE))
                .todayTokens(tokenRepository.countByCreatedAtBetween(startOfDay, endOfDay))
                .totalUsers(userRepository.countByRole(Role.USER))
                .totalAdmins(userRepository.countByRole(Role.ADMIN))
                .totalSuperAdmins(userRepository.countByRole(Role.SUPER_ADMIN))
                .totalActiveTokens(tokenRepository.countByStatus(Token.Status.ACTIVE))
                .totalTodayTokens(tokenRepository.countByCreatedAtBetween(startOfDay, endOfDay))
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userStats", key = "#userId", unless = "#result == null")
    public StatisticsDTO getUserStatistics(Long userId) {
        // Get today's date range in UTC
        LocalDateTime startOfDay = LocalDateTime.now(ZoneId.of("UTC")).with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now(ZoneId.of("UTC")).with(LocalTime.MAX);

        return StatisticsDTO.builder()
                .activeTokens(tokenRepository.countByUserIdAndStatus(userId, Token.Status.ACTIVE))
                .todayTokens(tokenRepository.countByUserIdAndCreatedAtBetween(userId, startOfDay, endOfDay))
                .build();
    }
} 