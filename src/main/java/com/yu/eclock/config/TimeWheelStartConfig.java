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
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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
