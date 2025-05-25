package com.example.linelessbackend.service;

import com.example.linelessbackend.dto.UserDTO;
import com.example.linelessbackend.dto.AdminStatsDTO;
import com.example.linelessbackend.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByEmail(String email);
    UserDTO createUser(User user);
    UserDTO updateUser(Long id, User user);
    void deleteUser(Long id);
    UserDTO updateUserPreferences(Long id, Map<String, Object> preferences);
    void updateLastLogin(Long id);
    AdminStatsDTO getAdminStats(Long adminId);
}