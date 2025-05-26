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
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO updateUserPreferences(Long id, Map<String, Object> preferences);
    void updateLastLogin(Long id);
    UserDTO updateUserRole(Long userId, String newRole);
    Map<String, Object> getSystemStats();
    AdminStatsDTO getAdminStats(Long adminId);
}