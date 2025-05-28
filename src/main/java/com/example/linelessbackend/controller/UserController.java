package com.example.linelessbackend.controller;

import com.example.linelessbackend.dto.AdminStatsDTO;
import com.example.linelessbackend.dto.UserDTO;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.service.StatisticsService;
import com.example.linelessbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/users", "/api/user"})
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or @userService.getUserById(#id).get().email == authentication.principal.username")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or #email == authentication.principal.username")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or @userService.getUserById(#id).get().email == authentication.principal.username")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/preferences")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or @userService.getUserById(#id).get().email == authentication.principal.username")
    public ResponseEntity<UserDTO> updateUserPreferences(
            @PathVariable Long id,
            @RequestBody Map<String, Object> preferences) {
        return ResponseEntity.ok(userService.updateUserPreferences(id, preferences));
    }

    @PutMapping("/{id}/last-login")
    public ResponseEntity<Void> updateLastLogin(@PathVariable Long id) {
        userService.updateLastLogin(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @RequestBody UserDTO userDTO,
            Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email)
                .map(user -> {
                    userDTO.setId(user.getId()); // Ensure we don't change the ID
                    return ResponseEntity.ok(userService.updateUser(user.getId(), userDTO));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<UserDTO> updateUserPreferences(
            @RequestBody Map<String, Object> preferences,
            Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(userService.updateUserPreferences(user.getId(), preferences)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me/stats")
    public ResponseEntity<AdminStatsDTO> getUserStats(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok(userService.getAdminStats(user.getId())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getUserStatsRedirect(Authentication authentication) {
        return getUserStats(authentication);
    }
} 