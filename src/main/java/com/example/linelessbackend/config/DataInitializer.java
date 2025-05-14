package com.example.linelessbackend.config;

import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.Role;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.repository.CompanyRepository;
import com.example.linelessbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create test user (normal user)
        if (!userRepository.existsByEmail("user@test.com")) {
            User testUser = new User();
            testUser.setName("Test User");
            testUser.setEmail("user@test.com");
            testUser.setPassword(passwordEncoder.encode("user123"));
            testUser.setRole(Role.USER);
            userRepository.save(testUser);
            System.out.println("Test user created successfully");
        }

        // Create admin user (company representative)
        User admin = null;
        if (!userRepository.existsByEmail("admin@test.com")) {
            admin = new User();
            admin.setName("Company Admin");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin = userRepository.save(admin);
            System.out.println("Company admin user created successfully");
        } else {
            admin = userRepository.findByEmail("admin@test.com").orElse(null);
        }

        // Create super admin (application moderator)
        if (!userRepository.existsByEmail("super@test.com")) {
            User superAdmin = new User();
            superAdmin.setName("Super Admin");
            superAdmin.setEmail("super@test.com");
            superAdmin.setPassword(passwordEncoder.encode("super123"));
            superAdmin.setRole(Role.SUPER_ADMIN);
            userRepository.save(superAdmin);
            System.out.println("Super admin user created successfully");
        }
        
        // Check for existing users and print their IDs
        System.out.println("Checking for existing users...");
        List<User> existingUsers = userRepository.findAll();
        System.out.println("Found " + existingUsers.size() + " users");

        for (User existingUser : existingUsers) {
            System.out.println("Existing user: ID=" + existingUser.getId() + 
                              ", Name=" + existingUser.getName() + 
                              ", Email=" + existingUser.getEmail() + 
                              ", Role=" + existingUser.getRole());
        }

        // Create a second admin user
        if (!userRepository.existsByEmail("admin2@test.com")) {
            User secondAdmin = new User();
            secondAdmin.setName("Second Admin");
            secondAdmin.setEmail("admin2@test.com");
            secondAdmin.setPassword(passwordEncoder.encode("admin456"));
            secondAdmin.setRole(Role.ADMIN);
            User savedUser = userRepository.save(secondAdmin);
            System.out.println("Second admin user created with ID: " + savedUser.getId());
        }
        
        // Create a test company
        System.out.println("Checking for existing companies...");
        List<Company> existingCompanies = companyRepository.findAll();
        System.out.println("Found " + existingCompanies.size() + " companies");

        for (Company existingCompany : existingCompanies) {
            System.out.println("Existing company: ID=" + existingCompany.getId() + ", Name=" + existingCompany.getName());
        }

        if (companyRepository.count() == 0) {
            Company company = new Company();
            company.setName("Test Company");
            company.setAddress("123 Test St, Test City");
            
            // Add admin to company if available
            if (admin != null) {
                company.addAdmin(admin);
                System.out.println("Adding admin to test company");
            }
            
            Company savedCompany = companyRepository.save(company);
            System.out.println("Test company created successfully with ID: " + savedCompany.getId());
        } else {
            // Ensure we have a company with ID 1
            if (companyRepository.findById(1L).isEmpty()) {
                System.out.println("No company with ID 1 found, creating one");
                Company company = new Company();
                company.setName("Main Company");
                company.setAddress("1 Main St, Main City");
                
                // Add admin to company if available
                if (admin != null) {
                    company.addAdmin(admin);
                    System.out.println("Adding admin to main company");
                }
                
                // Try to force ID 1 (may not work with all JPA providers)
                company.setId(1L);
                Company savedCompany = companyRepository.save(company);
                System.out.println("Main company created with ID: " + savedCompany.getId());
            }
        }
    }
} 