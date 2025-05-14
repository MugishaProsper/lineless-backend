package com.example.linelessbackend.service;

import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.Role;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.repository.CompanyRepository;
import com.example.linelessbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    @Transactional
    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    @Transactional
    public Company updateCompany(Long id, Company companyDetails) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id " + id));
        
        company.setName(companyDetails.getName());
        company.setAddress(companyDetails.getAddress());
        
        return companyRepository.save(company);
    }

    @Transactional
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id " + id));
        
        companyRepository.delete(company);
    }
    
    @Transactional
    public Company assignAdminToCompany(Long companyId, Long userId) {
        System.out.println("CompanyService: Assigning admin ID " + userId + " to company ID " + companyId);
        
        // First check if user exists
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id " + userId);
        }
        
        // Check if company exists
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new RuntimeException("Company not found with id " + companyId);
        }
        
        Company company = companyOpt.get();
        User user = userOpt.get();
        
        // Log the user and company details for debugging
        System.out.println("CompanyService: Found company: " + company.getName() + " (ID: " + company.getId() + ")");
        System.out.println("CompanyService: Found user: " + user.getName() + " (ID: " + user.getId() + ", Role: " + user.getRole() + ")");
        
        // Ensure the user is an ADMIN
        if (user.getRole() != Role.ADMIN) {
            System.out.println("CompanyService: Updating user role from " + user.getRole() + " to ADMIN");
            user.setRole(Role.ADMIN);
            userRepository.save(user);
        }
        
        // Check if admin is already assigned to the company
        if (company.getAdmins().contains(user)) {
            System.out.println("CompanyService: Admin is already assigned to this company");
            return company;
        }
        
        company.addAdmin(user);
        Company savedCompany = companyRepository.save(company);
        System.out.println("CompanyService: Successfully assigned admin to company");
        return savedCompany;
    }
    
    @Transactional
    public Company removeAdminFromCompany(Long companyId, Long userId) {
        System.out.println("CompanyService: Removing admin ID " + userId + " from company ID " + companyId);
        
        // First check if user exists
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with id " + userId);
        }
        
        // Check if company exists
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        if (companyOpt.isEmpty()) {
            throw new RuntimeException("Company not found with id " + companyId);
        }
        
        Company company = companyOpt.get();
        User user = userOpt.get();
        
        // Log the user and company details for debugging
        System.out.println("CompanyService: Found company: " + company.getName() + " (ID: " + company.getId() + ")");
        System.out.println("CompanyService: Found user: " + user.getName() + " (ID: " + user.getId() + ", Role: " + user.getRole() + ")");
        
        // Check if admin is assigned to the company
        if (!company.getAdmins().contains(user)) {
            System.out.println("CompanyService: Admin is not assigned to this company");
            return company;
        }
        
        company.removeAdmin(user);
        Company savedCompany = companyRepository.save(company);
        System.out.println("CompanyService: Successfully removed admin from company");
        return savedCompany;
    }
    
    @Transactional(readOnly = true)
    public Set<User> getCompanyAdmins(Long companyId) {
        System.out.println("CompanyService: Looking for company with ID: " + companyId);
        try {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company not found with id " + companyId));
            
            System.out.println("CompanyService: Found company: " + company.getName());
            System.out.println("CompanyService: Admins count: " + company.getAdmins().size());
            
            return company.getAdmins();
        } catch (Exception e) {
            System.err.println("CompanyService exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public List<Company> getCompaniesByAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found with id " + adminId));
        
        return companyRepository.findByAdminsContaining(admin);
    }
} 