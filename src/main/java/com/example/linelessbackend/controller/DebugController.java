package com.example.linelessbackend.controller;

import com.example.linelessbackend.dto.CompanyDTO;
import com.example.linelessbackend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }
} 