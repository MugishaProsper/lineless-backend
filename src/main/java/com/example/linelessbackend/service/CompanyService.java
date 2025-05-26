package com.example.linelessbackend.service;

import com.example.linelessbackend.dto.CompanyDTO;
import com.example.linelessbackend.dto.CompanyStatisticsDTO;
import com.example.linelessbackend.dto.UserDTO;

import java.util.List;
import java.util.Map;

public interface CompanyService {
    List<CompanyDTO> getAllCompanies();
    CompanyDTO getCompany(Long id);
    CompanyDTO createCompany(CompanyDTO companyDTO);
    CompanyDTO updateCompany(Long id, CompanyDTO companyDTO);
    void deleteCompany(Long id);
    List<UserDTO> getCompanyAdmins(Long id);
    CompanyDTO assignAdmin(Long companyId, Long adminId);
    CompanyDTO removeAdmin(Long companyId, Long adminId);
    CompanyStatisticsDTO getCompanyStatistics(Long id);
    List<CompanyDTO> getCompaniesByAdmin(Long adminId);
    CompanyDTO updateCompanySettings(Long id, CompanyDTO companyDTO);
} 