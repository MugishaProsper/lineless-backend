package com.example.linelessbackend.service;

import com.example.linelessbackend.dto.CompanyDTO;
import com.example.linelessbackend.dto.CompanyStatisticsDTO;
import java.util.List;

public interface CompanyService {
    CompanyDTO createCompany(CompanyDTO companyDTO);
    CompanyDTO updateCompany(Long id, CompanyDTO companyDTO);
    List<CompanyDTO> getAllCompanies();
    CompanyDTO getCompany(Long id);
    void deleteCompany(Long id);
    CompanyDTO updateCompanySettings(Long id, CompanyDTO companyDTO);
    CompanyDTO assignAdmin(Long id, Long adminId);
    CompanyStatisticsDTO getCompanyStatistics(Long id);
    List<CompanyDTO> getCompaniesByAdmin(Long adminId);
} 