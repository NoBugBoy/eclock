package com.yu.eclock.config;

import com.yu.eclock.core.PrintBanner;
import com.yu.eclock.core.TimeWheel;
import com.yu.eclock.core.TimeWheelStartHandler;
import com.yu.eclock.listener.StartedUpTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnClass({TimeWheelStartConfig.class})
@EnableConfigurationProperties(TimeWheelStartConfig.class)
public class TimeWheelConfig {
    @Value("${version}")
    private String version;
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeWheelConfig.class);
    private final  TimeWheelStartConfig timeWheelStartConfig;

    public TimeWheelConfig(TimeWheelStartConfig timeWheelStartConfig) {
        this.timeWheelStartConfig = timeWheelStartConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "eternal.clock", value = "enabled", havingValue = "true")
    public TimeWheel timeWheel(){
        return new TimeWheel(false,timeWheelStartConfig);
    }

    @Bean(name = "eclock-pool")
    @ConditionalOnProperty(prefix = "eternal.clock", value = "enabled", havingValue = "true")
    public ThreadPoolTaskExecutor wheelPoll(){
        LOGGER.info("eclock-pool init ...");
        ThreadPoolTaskExecutor taskPool = new ThreadPoolTaskExecutor();
        taskPool.setCorePoolSize(
            Math.min(timeWheelStartConfig.getThreads().getCore(), Runtime.getRuntime().availableProcessors()));
        taskPool.setKeepAliveSeconds(timeWheelStartConfig.getThreads().getKeepAliveSeconds());
        taskPool.setMaxPoolSize(timeWheelStartConfig.getThreads().getMax());
        taskPool.setThreadNamePrefix("eclock-task-");
        taskPool.setDaemon(true);
        return taskPool;
    }
    @Bean
    @ConditionalOnProperty(prefix = "eternal.clock", value = "enabled", havingValue = "true")
    public StartedUpTaskHandler startedUpTaskHandler(){
        return new StartedUpTaskHandler(timeWheel(), timeWheelStartConfig);
    }

    @ConditionalOnProperty(prefix = "eternal.clock", value = "enabled", havingValue = "true")
    @Bean
    public TimeWheelStartHandler timeWheelStartHandler(){
        PrintBanner.print(version);
        final TimeWheelStartHandler timeWheelStartHandler = new TimeWheelStartHandler(timeWheel(),wheelPoll(),timeWheelStartConfig);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new SynchronousQueue<>(),
            r -> {
                Thread t= new Thread(r);
                // if you want debug this project please set Daemon false
                t.setDaemon(false);
                t.setName("eclock");
                return t;
            });
        threadPoolExecutor.execute(timeWheelStartHandler);
        LOGGER.info("eternal-clock started ...");
        return timeWheelStartHandler;
    }
    //-------
    // @Bean
    // @ConditionalOnMissingBean
    // @ConditionalOnProperty(prefix = "eternal.clock.persistence", value = "name", havingValue = "mongo")
    // public MongoTemplate mongoPersistence(){
    //     MongoPersistence mongoPersistence = new MongoPersistence(timeWheelStartConfig.getPersistence().getDbUrl(),timeWheelStartConfig.getPersistence().getDbName());
    //     return mongoPersistence.getTemplate();
    // }

}
