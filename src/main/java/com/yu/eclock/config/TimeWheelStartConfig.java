package com.yu.eclock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eternal.clock")
public class TimeWheelStartConfig {
    private volatile boolean enabled;
    private Threads threads;
    private Persistence persistence;

    public static class Threads{
        Threads(){}
        private int core= Runtime.getRuntime().availableProcessors();
        private int max = core * 2;
        private int keepAliveSeconds = 10;


        public int getCore() {
            return core;
        }

        public void setCore(int core) {
            this.core = core;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public int getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(int keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }
    }
    public static class Persistence{
        Persistence(){}
        private boolean enabled = false;
        private String name = "mongo";
        private String dbName = "eclock";
        private String dbUrl = "mongodb://localhost:27017";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getDbUrl() {
            return dbUrl;
        }

        public void setDbUrl(String dbUrl) {
            this.dbUrl = dbUrl;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public void setThreads(Threads threads) {
        this.threads = threads;
    }

    public Threads getThreads() {
        return threads;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }
}
