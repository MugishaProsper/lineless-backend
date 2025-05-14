package com.example.linelessbackend.controller;

import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    private final CompanyService companyService;
    
    public DebugController(CompanyService companyService) {
        this.companyService = companyService;
    }
    
    @GetMapping("/companies")
    public ResponseEntity<String> debugCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        StringBuilder sb = new StringBuilder("Available companies:\n");
        
        for (Company company : companies) {
            sb.append("ID: ").append(company.getId())
              .append(", Name: ").append(company.getName())
              .append(", Admins: ").append(company.getAdmins().size())
              .append("\n");
        }
        
        if (companies.isEmpty()) {
            sb.append("No companies found in the database.");
        }
        
        return ResponseEntity.ok(sb.toString());
    }
} 