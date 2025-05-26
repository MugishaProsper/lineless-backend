package com.example.linelessbackend.controller;

import com.example.linelessbackend.dto.CompanyDTO;
import com.example.linelessbackend.dto.UserDTO;
import com.example.linelessbackend.service.CompanyService;
import com.example.linelessbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        String newRole = request.get("role");
        return ResponseEntity.ok(userService.updateUserRole(userId, newRole));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        return ResponseEntity.ok(userService.getSystemStats());
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }

    @PostMapping("/companies")
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.createCompany(companyDTO));
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
            @PathVariable Long id,
            @RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.updateCompany(id, companyDTO));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/companies/{id}/admins")
    public ResponseEntity<List<UserDTO>> getCompanyAdmins(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyAdmins(id));
    }

    @PostMapping("/companies/{companyId}/admins/{adminId}")
    public ResponseEntity<CompanyDTO> assignAdminToCompany(
            @PathVariable Long companyId,
            @PathVariable Long adminId) {
        return ResponseEntity.ok(companyService.assignAdmin(companyId, adminId));
    }

    @DeleteMapping("/companies/{companyId}/admins/{adminId}")
    public ResponseEntity<CompanyDTO> removeAdminFromCompany(
            @PathVariable Long companyId,
            @PathVariable Long adminId) {
        return ResponseEntity.ok(companyService.removeAdmin(companyId, adminId));
    }
} 