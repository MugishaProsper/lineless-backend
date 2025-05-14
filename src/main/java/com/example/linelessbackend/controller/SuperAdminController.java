package com.example.linelessbackend.controller;

import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.service.CompanyService;
import com.example.linelessbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {
    private final UserService userService;
    private final CompanyService companyService;

    public SuperAdminController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    // User Management
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String role = request.get("role");
        User updatedUser = userService.updateUserRole(userId, role);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    // System Statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        Map<String, Object> stats = userService.getSystemStats();
        return ResponseEntity.ok(stats);
    }

    // Company Management
    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        Company createdCompany = companyService.createCompany(company);
        return ResponseEntity.ok(createdCompany);
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable Long id, @RequestBody Company companyDetails) {
        Company updatedCompany = companyService.updateCompany(id, companyDetails);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok().build();
    }
    
    // Company Admin Management
    @GetMapping("/companies/{companyId}/admins")
    public ResponseEntity<Set<User>> getCompanyAdmins(@PathVariable Long companyId) {
        System.out.println("Fetching admins for company ID: " + companyId);
        try {
            // Check if company exists
            Optional<Company> companyOpt = companyService.getCompanyById(companyId);
            
            // If company doesn't exist and ID is 1, create it
            if (companyOpt.isEmpty() && companyId == 1L) {
                System.out.println("Company with ID 1 not found - creating it on the fly");
                Company newCompany = new Company();
                newCompany.setName("Default Company");
                newCompany.setAddress("Created automatically");
                Company savedCompany = companyService.createCompany(newCompany);
                System.out.println("Created company with ID: " + savedCompany.getId());
                
                // If the new company doesn't have ID 1, log a warning
                if (savedCompany.getId() != 1L) {
                    System.out.println("WARNING: Created company has ID " + savedCompany.getId() + " instead of 1");
                }
                
                return ResponseEntity.ok(savedCompany.getAdmins());
            }
            
            Set<User> admins = companyService.getCompanyAdmins(companyId);
            System.out.println("Found " + admins.size() + " admins for company ID: " + companyId);
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            System.err.println("Error fetching admins for company ID " + companyId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/companies/{companyId}/admins/{userId}")
    public ResponseEntity<Company> assignAdminToCompany(
            @PathVariable Long companyId, 
            @PathVariable Long userId) {
        System.out.println("Assigning admin ID " + userId + " to company ID: " + companyId);
        
        try {
            // Check if company exists
            Optional<Company> companyOpt = companyService.getCompanyById(companyId);
            
            // If company doesn't exist and ID is 1, create it
            if (companyOpt.isEmpty() && companyId == 1L) {
                System.out.println("Company with ID 1 not found - creating it on the fly for admin assignment");
                Company newCompany = new Company();
                newCompany.setName("Default Company");
                newCompany.setAddress("Created automatically");
                Company savedCompany = companyService.createCompany(newCompany);
                System.out.println("Created company with ID: " + savedCompany.getId());
                
                // If the new company has a different ID than expected, log a warning
                if (savedCompany.getId() != 1L) {
                    System.out.println("WARNING: Created company has ID " + savedCompany.getId() + " instead of " + companyId);
                    // Use the actual ID of the created company
                    companyId = savedCompany.getId();
                }
            }
            
            Company company = companyService.assignAdminToCompany(companyId, userId);
            return ResponseEntity.ok(company);
        } catch (Exception e) {
            System.err.println("Error assigning admin " + userId + " to company " + companyId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/companies/{companyId}/admins/{userId}")
    public ResponseEntity<Company> removeAdminFromCompany(
            @PathVariable Long companyId, 
            @PathVariable Long userId) {
        System.out.println("Removing admin ID " + userId + " from company ID: " + companyId);
        
        try {
            // Check if company exists
            Optional<Company> companyOpt = companyService.getCompanyById(companyId);
            
            // If company doesn't exist, return 404
            if (companyOpt.isEmpty()) {
                System.out.println("Cannot remove admin: Company with ID " + companyId + " not found");
                return ResponseEntity.notFound().build();
            }
            
            Company company = companyService.removeAdminFromCompany(companyId, userId);
            return ResponseEntity.ok(company);
        } catch (Exception e) {
            System.err.println("Error removing admin " + userId + " from company " + companyId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
} 