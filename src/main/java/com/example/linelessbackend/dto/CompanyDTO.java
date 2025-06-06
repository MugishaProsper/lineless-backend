package com.example.linelessbackend.dto;

import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.User;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CompanyDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String address;
    private String contactEmail;
    private String contactPhone;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<Long> adminIds = new HashSet<>();

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<Long> getAdminIds() { return adminIds; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCode(String code) { this.code = code; }
    public void setDescription(String description) { this.description = description; }
    public void setAddress(String address) { this.address = address; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setAdminIds(Set<Long> adminIds) { this.adminIds = adminIds; }

    public static CompanyDTO fromCompany(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setCode(company.getCode());
        dto.setDescription(company.getDescription());
        dto.setAddress(company.getAddress());
        dto.setContactEmail(company.getContactEmail());
        dto.setContactPhone(company.getContactPhone());
        dto.setActive(company.isActive());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        
        // Convert Set<User> to Set<Long> for admin IDs
        if (company.getAdmins() != null) {
            dto.setAdminIds(company.getAdmins().stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
        }
        return dto;
    }
} 