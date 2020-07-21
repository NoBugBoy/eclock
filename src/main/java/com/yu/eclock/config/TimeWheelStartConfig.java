package com.yu.eclock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eternal.clock")
public class TimeWheelStartConfig {
    private volatile boolean enabled;
    private Threads threads;

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
}
