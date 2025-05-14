package com.example.linelessbackend.dto;

public class StatisticsDTO {
    // Common statistics
    private Long totalQueues;
    private Long activeTokens;
    private Long todayTokens;

    // Admin-specific statistics
    private Long managedQueues;
    private Long managedActiveTokens;
    private Long managedTodayTokens;

    // Super admin statistics
    private Long totalUsers;
    private Long totalAdmins;
    private Long totalSuperAdmins;
    private Long totalActiveTokens;
    private Long totalTodayTokens;

    public StatisticsDTO() {}

    public StatisticsDTO(Long totalQueues, Long activeTokens, Long todayTokens,
                        Long managedQueues, Long managedActiveTokens, Long managedTodayTokens,
                        Long totalUsers, Long totalAdmins, Long totalSuperAdmins,
                        Long totalActiveTokens, Long totalTodayTokens) {
        this.totalQueues = totalQueues;
        this.activeTokens = activeTokens;
        this.todayTokens = todayTokens;
        this.managedQueues = managedQueues;
        this.managedActiveTokens = managedActiveTokens;
        this.managedTodayTokens = managedTodayTokens;
        this.totalUsers = totalUsers;
        this.totalAdmins = totalAdmins;
        this.totalSuperAdmins = totalSuperAdmins;
        this.totalActiveTokens = totalActiveTokens;
        this.totalTodayTokens = totalTodayTokens;
    }

    public Long getTotalQueues() {
        return totalQueues;
    }

    public void setTotalQueues(Long totalQueues) {
        this.totalQueues = totalQueues;
    }

    public Long getActiveTokens() {
        return activeTokens;
    }

    public void setActiveTokens(Long activeTokens) {
        this.activeTokens = activeTokens;
    }

    public Long getTodayTokens() {
        return todayTokens;
    }

    public void setTodayTokens(Long todayTokens) {
        this.todayTokens = todayTokens;
    }

    public Long getManagedQueues() {
        return managedQueues;
    }

    public void setManagedQueues(Long managedQueues) {
        this.managedQueues = managedQueues;
    }

    public Long getManagedActiveTokens() {
        return managedActiveTokens;
    }

    public void setManagedActiveTokens(Long managedActiveTokens) {
        this.managedActiveTokens = managedActiveTokens;
    }

    public Long getManagedTodayTokens() {
        return managedTodayTokens;
    }

    public void setManagedTodayTokens(Long managedTodayTokens) {
        this.managedTodayTokens = managedTodayTokens;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(Long totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    public Long getTotalSuperAdmins() {
        return totalSuperAdmins;
    }

    public void setTotalSuperAdmins(Long totalSuperAdmins) {
        this.totalSuperAdmins = totalSuperAdmins;
    }

    public Long getTotalActiveTokens() {
        return totalActiveTokens;
    }

    public void setTotalActiveTokens(Long totalActiveTokens) {
        this.totalActiveTokens = totalActiveTokens;
    }

    public Long getTotalTodayTokens() {
        return totalTodayTokens;
    }

    public void setTotalTodayTokens(Long totalTodayTokens) {
        this.totalTodayTokens = totalTodayTokens;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long totalQueues;
        private Long activeTokens;
        private Long todayTokens;
        private Long managedQueues;
        private Long managedActiveTokens;
        private Long managedTodayTokens;
        private Long totalUsers;
        private Long totalAdmins;
        private Long totalSuperAdmins;
        private Long totalActiveTokens;
        private Long totalTodayTokens;

        public Builder totalQueues(Long totalQueues) {
            this.totalQueues = totalQueues;
            return this;
        }

        public Builder activeTokens(Long activeTokens) {
            this.activeTokens = activeTokens;
            return this;
        }

        public Builder todayTokens(Long todayTokens) {
            this.todayTokens = todayTokens;
            return this;
        }

        public Builder managedQueues(Long managedQueues) {
            this.managedQueues = managedQueues;
            return this;
        }

        public Builder managedActiveTokens(Long managedActiveTokens) {
            this.managedActiveTokens = managedActiveTokens;
            return this;
        }

        public Builder managedTodayTokens(Long managedTodayTokens) {
            this.managedTodayTokens = managedTodayTokens;
            return this;
        }

        public Builder totalUsers(Long totalUsers) {
            this.totalUsers = totalUsers;
            return this;
        }

        public Builder totalAdmins(Long totalAdmins) {
            this.totalAdmins = totalAdmins;
            return this;
        }

        public Builder totalSuperAdmins(Long totalSuperAdmins) {
            this.totalSuperAdmins = totalSuperAdmins;
            return this;
        }

        public Builder totalActiveTokens(Long totalActiveTokens) {
            this.totalActiveTokens = totalActiveTokens;
            return this;
        }

        public Builder totalTodayTokens(Long totalTodayTokens) {
            this.totalTodayTokens = totalTodayTokens;
            return this;
        }

        public StatisticsDTO build() {
            return new StatisticsDTO(totalQueues, activeTokens, todayTokens,
                    managedQueues, managedActiveTokens, managedTodayTokens,
                    totalUsers, totalAdmins, totalSuperAdmins,
                    totalActiveTokens, totalTodayTokens);
        }
    }
} 