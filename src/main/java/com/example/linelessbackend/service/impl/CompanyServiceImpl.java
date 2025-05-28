package com.example.linelessbackend.service.impl;

import com.example.linelessbackend.dto.CompanyDTO;
import com.example.linelessbackend.dto.CompanyStatisticsDTO;
import com.example.linelessbackend.dto.UserDTO;
import com.example.linelessbackend.exception.ResourceNotFoundException;
import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.Queue;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.repository.CompanyRepository;
import com.example.linelessbackend.repository.UserRepository;
import com.example.linelessbackend.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = new Company();
        updateCompanyFromDTO(company, companyDTO);
        return convertToDTO(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyDTO updateCompany(Long id, CompanyDTO companyDTO) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        updateCompanyFromDTO(company, companyDTO);
        return convertToDTO(companyRepository.save(company));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyDTO getCompany(Long id) {
        return companyRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        companyRepository.delete(company);
    }

    @Override
    @Transactional
    public CompanyDTO updateCompanySettings(Long id, CompanyDTO companyDTO) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        
        // Only update settings-related fields
        company.setDescription(companyDTO.getDescription());
        company.setContactEmail(companyDTO.getContactEmail());
        company.setContactPhone(companyDTO.getContactPhone());
        
        return convertToDTO(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyDTO assignAdmin(Long companyId, Long adminId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + adminId));
        
        company.getAdmins().add(admin);
        return convertToDTO(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyDTO removeAdmin(Long companyId, Long adminId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + adminId));
        
        company.getAdmins().remove(admin);
        return convertToDTO(companyRepository.save(company));
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyStatisticsDTO getCompanyStatistics(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        CompanyStatisticsDTO stats = new CompanyStatisticsDTO();
        stats.setCompanyId(company.getId());
        stats.setCompanyName(company.getName());
        
        // Get queue statistics
        List<Queue> queues = company.getQueues();
        stats.setTotalQueues(queues.size());
        stats.setActiveQueues((int) queues.stream()
                .filter(Queue::getIsActive)
                .count());

        // Get token statistics
        int totalTokens = 0;
        int activeTokens = 0;
        int completedTokens = 0;
        int cancelledTokens = 0;
        long totalWaitTime = 0;
        int processedTokens = 0;

        for (Queue queue : queues) {
            List<Token> tokens = queue.getTokens();
            totalTokens += tokens.size();
            
            for (Token token : tokens) {
                switch (token.getStatus()) {
                    case ACTIVE:
                        activeTokens++;
                        break;
                    case COMPLETED:
                        completedTokens++;
                        if (token.getCompletedAt() != null && token.getCreatedAt() != null) {
                            totalWaitTime += java.time.Duration.between(
                                token.getCreatedAt(),
                                token.getCompletedAt()
                            ).toMinutes();
                            processedTokens++;
                        }
                        break;
                    case CANCELLED:
                        cancelledTokens++;
                        break;
                }
            }
        }

        stats.setTotalTokens(totalTokens);
        stats.setActiveTokens(activeTokens);
        stats.setCompletedTokens(completedTokens);
        stats.setCancelledTokens(cancelledTokens);
        stats.setAverageWaitTime(processedTokens > 0 ? (double) totalWaitTime / processedTokens : 0);
        stats.setLastUpdated(LocalDateTime.now());

        return stats;
    }

    @Override
    public List<CompanyDTO> getCompaniesByAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + adminId));
        
        return companyRepository.findByAdminsContaining(admin).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getCompanyAdmins(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        
        return company.getAdmins().stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    private CompanyDTO convertToDTO(Company company) {
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
        return dto;
    }

    private void updateCompanyFromDTO(Company company, CompanyDTO dto) {
        company.setName(dto.getName());
        company.setCode(dto.getCode());
        company.setDescription(dto.getDescription());
        company.setAddress(dto.getAddress());
        company.setContactEmail(dto.getContactEmail());
        company.setContactPhone(dto.getContactPhone());
        company.setActive(dto.isActive());
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setLastLogin(user.getLastLogin());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setPreferences(user.getPreferences());
        return dto;
    }
} 