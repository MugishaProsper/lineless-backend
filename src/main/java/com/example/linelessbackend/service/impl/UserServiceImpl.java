package com.example.linelessbackend.service.impl;

import com.example.linelessbackend.dto.AdminStatsDTO;
import com.example.linelessbackend.dto.UserDTO;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.repository.UserRepository;
import com.example.linelessbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::fromUser);
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        // TODO: Implement actual logic
        return Optional.empty();
    }

    @Override
    @Transactional
    public UserDTO createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setCreatedAt(LocalDateTime.now());
        return UserDTO.fromUser(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(userDetails.getPassword());
        }

        return UserDTO.fromUser(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public UserDTO updateUserPreferences(Long id, Map<String, Object> preferences) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setPreferences(preferences);
        return UserDTO.fromUser(userRepository.save(user));
    }

    @Override
    @Transactional
    public void updateLastLogin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public AdminStatsDTO getAdminStats(Long adminId) {
        // TODO: Implement actual logic
        return null;
    }
} 