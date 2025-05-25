package com.example.linelessbackend.service;

import com.example.linelessbackend.dto.QueueDTO;
import com.example.linelessbackend.model.Company;
import com.example.linelessbackend.model.Queue;
import com.example.linelessbackend.model.Token;
import com.example.linelessbackend.model.User;
import com.example.linelessbackend.exception.ResourceNotFoundException;
import com.example.linelessbackend.exception.UnauthorizedException;
import com.example.linelessbackend.repository.CompanyRepository;
import com.example.linelessbackend.repository.QueueRepository;
import com.example.linelessbackend.repository.TokenRepository;
import com.example.linelessbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueueService {
    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);
    
    @Autowired
    private QueueRepository queueRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TokenRepository tokenRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    // Queue management methods
    public List<QueueDTO> getAvailableQueues() {
        return queueRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QueueDTO> getAdminQueues(Long adminId) {
        return queueRepository.findByAdminId(adminId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<QueueDTO> getQueuesByAdmin(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        List<QueueDTO> result = new ArrayList<>();
        
        // First get queues directly associated with the admin
        List<QueueDTO> directQueues = queueRepository.findByAdminId(adminId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        result.addAll(directQueues);
        
        // Then get queues from companies where the admin is assigned
        List<Company> adminCompanies = companyRepository.findByAdminsContaining(admin);
        for (Company company : adminCompanies) {
            List<Queue> companyQueues = queueRepository.findByCompanyId(company.getId());
            List<QueueDTO> companyQueueDTOs = companyQueues.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            result.addAll(companyQueueDTOs);
        }
        
        return result;
    }
    
    public boolean isQueueOwnedByAdmin(Long queueId, Long adminId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));
        
        // Check if admin is directly assigned to the queue
        if (queue.getAdmin().getId().equals(adminId)) {
            return true;
        }
        
        // Check if admin belongs to the company that owns the queue
        if (queue.getCompany() != null) {
            User admin = userRepository.findById(adminId)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
            
            return queue.getCompany().getAdmins().contains(admin);
        }
        
        return false;
    }

    @Transactional
    public QueueDTO createQueue(QueueDTO queueDTO, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        Queue queue = new Queue();
        queue.setName(queueDTO.getName());
        queue.setDescription(queueDTO.getDescription());
        queue.setAdmin(admin);
        queue.setIsActive(true);
        
        // Set start and end times if provided
        if (queueDTO.getStartTime() != null && !queueDTO.getStartTime().isEmpty()) {
            queue.setStartTime(LocalDateTime.parse(queueDTO.getStartTime(), formatter));
        }
        
        if (queueDTO.getEndTime() != null && !queueDTO.getEndTime().isEmpty()) {
            queue.setEndTime(LocalDateTime.parse(queueDTO.getEndTime(), formatter));
        }
        
        // Set company if provided
        if (queueDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(queueDTO.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
            queue.setCompany(company);
        }

        return convertToDTO(queueRepository.save(queue));
    }

    @Transactional
    public QueueDTO updateQueue(Long id, QueueDTO queueDTO) {
        Queue queue = queueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));

        queue.setName(queueDTO.getName());
        queue.setDescription(queueDTO.getDescription());
        queue.setIsActive(queueDTO.getIsActive());
        
        // Update start and end times if provided
        if (queueDTO.getStartTime() != null && !queueDTO.getStartTime().isEmpty()) {
            queue.setStartTime(LocalDateTime.parse(queueDTO.getStartTime(), formatter));
        }
        
        if (queueDTO.getEndTime() != null && !queueDTO.getEndTime().isEmpty()) {
            queue.setEndTime(LocalDateTime.parse(queueDTO.getEndTime(), formatter));
        }
        
        // Update company if provided
        if (queueDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(queueDTO.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
            queue.setCompany(company);
        }

        return convertToDTO(queueRepository.save(queue));
    }

    @Transactional
    public void deleteQueue(Long id) {
        Queue queue = queueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));
        queueRepository.delete(queue);
    }

    public List<QueueDTO> getAllQueues() {
        return queueRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public QueueDTO createQueueWithAdmin(QueueDTO queueDTO) {
        User admin = userRepository.findById(queueDTO.getAdminId())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        Queue queue = new Queue();
        queue.setName(queueDTO.getName());
        queue.setDescription(queueDTO.getDescription());
        queue.setAdmin(admin);
        queue.setIsActive(true);
        
        // Set start and end times if provided
        if (queueDTO.getStartTime() != null && !queueDTO.getStartTime().isEmpty()) {
            queue.setStartTime(LocalDateTime.parse(queueDTO.getStartTime(), formatter));
        }
        
        if (queueDTO.getEndTime() != null && !queueDTO.getEndTime().isEmpty()) {
            queue.setEndTime(LocalDateTime.parse(queueDTO.getEndTime(), formatter));
        }
        
        // Set company if provided
        if (queueDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(queueDTO.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
            queue.setCompany(company);
        }

        return convertToDTO(queueRepository.save(queue));
    }
    
    @Transactional
    public QueueDTO assignQueueToCompany(Long queueId, Long companyId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));
                
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
                
        queue.setCompany(company);
        
        return convertToDTO(queueRepository.save(queue));
    }

    @Transactional
    public QueueDTO assignAdminToQueue(Long id, Long adminId) {
        Queue queue = queueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        queue.setAdmin(admin);
        
        return convertToDTO(queueRepository.save(queue));
    }

    @Transactional
    public void deleteQueueAsSuperAdmin(Long id) {
        Queue queue = queueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));
        
        queueRepository.delete(queue);
    }

    // Token management methods
    @Transactional
    public Token issueToken(Long queueId, Long userId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));

        if (!queue.getIsActive()) {
            throw new UnauthorizedException("Queue is not active");
        }

        // Check for existing tokens
        List<Token> existingTokens = tokenRepository.findByQueueIdAndUserId(queueId, userId);
        long activeTokens = existingTokens.stream()
                .filter(token -> token.getStatus() == Token.Status.WAITING || token.getStatus() == Token.Status.ACTIVE)
                .count();

        if (activeTokens >= 2) {
            throw new UnauthorizedException("You already have 2 active tokens for this queue");
        }

        Token token = new Token();
        token.setQueue(queue);
        token.setId(userId);
        token.setPosition((int) (tokenRepository.countByQueueId(queueId) + 1));
        token.setStatus(Token.Status.WAITING);
        token.setRequestedAt(LocalDateTime.now());
        return tokenRepository.save(token);
    }

    @Transactional
    public Token serveNextToken(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));

        Token currentToken = tokenRepository.findByQueueIdAndStatusOrderByPositionAsc(queueId, Token.Status.ACTIVE)
                .stream()
                .findFirst()
                .orElse(null);

        if (currentToken != null) {
            currentToken.setStatus(Token.Status.COMPLETED);
            currentToken.setCompletedAt(LocalDateTime.now());
            tokenRepository.save(currentToken);
        }

        Token nextToken = tokenRepository.findByQueueIdAndStatusOrderByPositionAsc(queueId, Token.Status.WAITING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No waiting tokens in queue"));

        nextToken.setStatus(Token.Status.ACTIVE);
        nextToken.setStartedAt(LocalDateTime.now());
        return tokenRepository.save(nextToken);
    }

    public List<Token> getQueueTokens(Long queueId) {
        return tokenRepository.findByQueueId(queueId);
    }

    public List<Token> getUserTokens(Long userId) {
        return tokenRepository.findByUserId(userId);
    }

    @Transactional
    public void cancelToken(Long tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        if (token.getStatus() != Token.Status.WAITING) {
            throw new UnauthorizedException("Can only cancel waiting tokens");
        }

        token.setStatus(Token.Status.CANCELLED);
        tokenRepository.save(token);
    }

    @Transactional
    public void skipToken(Long tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));

        if (token.getStatus() != Token.Status.WAITING) {
            throw new UnauthorizedException("Can only skip waiting tokens");
        }

        token.setStatus(Token.Status.SKIPPED);
        tokenRepository.save(token);
    }

    @Transactional
    public void closeQueue(Long queueId) {
        Queue queue = queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue not found"));

        queue.setIsActive(false);
        queueRepository.save(queue);
    }

    private QueueDTO convertToDTO(Queue queue) {
        QueueDTO dto = new QueueDTO();
        dto.setId(queue.getId());
        dto.setName(queue.getName());
        dto.setDescription(queue.getDescription());
        dto.setAdminId(queue.getAdmin().getId());
        dto.setAdminName(queue.getAdmin().getName());
        
        if (queue.getCompany() != null) {
            dto.setCompanyId(queue.getCompany().getId());
            dto.setCompanyName(queue.getCompany().getName());
        }
        
        dto.setCurrentToken(queue.getCurrentToken());
        dto.setWaitingCount(queue.getWaitingCount());
        dto.setIsActive(queue.getIsActive());
        
        if (queue.getStartTime() != null) {
            dto.setStartTime(queue.getStartTime().format(formatter));
        }
        
        if (queue.getEndTime() != null) {
            dto.setEndTime(queue.getEndTime().format(formatter));
        }
        
        dto.setCreatedAt(queue.getCreatedAt().format(formatter));
        dto.setUpdatedAt(queue.getUpdatedAt().format(formatter));
        return dto;
    }
}