package com.example.linelessbackend.dto;

public class QueueDTO {
    private Long id;
    private String name;
    private String description;
    private Long adminId;
    private String adminName;
    private Long companyId;
    private String companyName;
    private String currentToken;
    private Long waitingCount;
    private boolean isActive;
    private String startTime;
    private String endTime;
    private String createdAt;
    private String updatedAt;

    public QueueDTO() {}

    public QueueDTO(Long id, String name, String description, Long adminId, String adminName,
                   Long companyId, String companyName, String currentToken, Long waitingCount, 
                   boolean isActive, String startTime, String endTime, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.adminId = adminId;
        this.adminName = adminName;
        this.companyId = companyId;
        this.companyName = companyName;
        this.currentToken = currentToken;
        this.waitingCount = waitingCount;
        this.isActive = isActive;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // No-arg constructor required
    public QueueDTO(Long id, String name, String description, Long adminId, String adminName,
                  String currentToken, Long waitingCount, boolean isActive,
                  String createdAt, String updatedAt) {
        this(id, name, description, adminId, adminName, null, null, currentToken, 
             waitingCount, isActive, null, null, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
    
    public Long getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public void setCurrentToken(String currentToken) {
        this.currentToken = currentToken;
    }

    public Long getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(Long waitingCount) {
        this.waitingCount = waitingCount;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private Long adminId;
        private String adminName;
        private Long companyId;
        private String companyName;
        private String currentToken;
        private Long waitingCount;
        private boolean isActive;
        private String startTime;
        private String endTime;
        private String createdAt;
        private String updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder adminId(Long adminId) {
            this.adminId = adminId;
            return this;
        }

        public Builder adminName(String adminName) {
            this.adminName = adminName;
            return this;
        }

        public Builder companyId(Long companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder currentToken(String currentToken) {
            this.currentToken = currentToken;
            return this;
        }

        public Builder waitingCount(Long waitingCount) {
            this.waitingCount = waitingCount;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder startTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public QueueDTO build() {
            return new QueueDTO(id, name, description, adminId, adminName,
                    companyId, companyName, currentToken, waitingCount, isActive, startTime, endTime, createdAt, updatedAt);
        }
    }
} 