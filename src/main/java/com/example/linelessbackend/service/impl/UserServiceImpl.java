package com.example.linelessbackend.service.impl;

import com.example.linelessbackend.dto.AdminStatsDTO;
import com.example.linelessbackend.dto.UserDTO;
import com.example.linelessbackend.exception.ResourceNotFoundException;
import com.example.linelessbackend.model.Role;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.repository.UserRepository;
import com.example.linelessbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        updateUserFromDTO(user, userDTO);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        updateUserFromDTO(user, userDTO);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO updateUserPreferences(Long id, Map<String, Object> preferences) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setPreferences(preferences);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void updateLastLogin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        try {
            Role role = Role.valueOf(newRole.toUpperCase());
            user.setRole(role);
            return convertToDTO(userRepository.save(user));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + newRole);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get total users
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        // Get users by role
        Map<Role, Long> usersByRole = userRepository.findAll().stream()
                .collect(Collectors.groupingBy(User::getRole, Collectors.counting()));
        stats.put("usersByRole", usersByRole);
        
        // Get active users (users who logged in within the last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long activeUsers = userRepository.findAll().stream()
                .filter(user -> user.getLastLogin() != null && user.getLastLogin().isAfter(thirtyDaysAgo))
                .count();
        stats.put("activeUsers", activeUsers);
        
        // Get new users in the last 30 days
        long newUsers = userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();
        stats.put("newUsers", newUsers);
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatsDTO getAdminStats(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + adminId));
        
        AdminStatsDTO stats = new AdminStatsDTO();
        stats.setAdminId(admin.getId());
        stats.setAdminName(admin.getName());
        stats.setAdminEmail(admin.getEmail());
        stats.setLastLogin(admin.getLastLogin());
        
        // Add more admin-specific statistics as needed
        
        return stats;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }

    private void updateUserFromDTO(User user, UserDTO dto) {
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        if (dto.getRole() != null) {
            user.setRole(Role.valueOf(dto.getRole()));
        }
    }
} 