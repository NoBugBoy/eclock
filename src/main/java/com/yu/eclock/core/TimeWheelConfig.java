package com.yu.eclock.core;

import com.yu.eclock.config.TimeWheelStartConfig;
import com.yu.eclock.exception.PersistenceInstanceException;
import com.yu.eclock.listener.StartedUpTaskHandler;
import com.yu.eclock.persistence.DataModel;
import com.yu.eclock.persistence.Persistence;
import com.yu.eclock.persistence.PersistenceFactory;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
@Configuration
@ConditionalOnClass({TimeWheelStartConfig.class})
@EnableConfigurationProperties(TimeWheelStartConfig.class)
public class TimeWheelConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeWheelConfig.class);
    private final  TimeWheelStartConfig timeWheelStartConfig;

    public TimeWheelConfig(TimeWheelStartConfig timeWheelStartConfig) {
        this.timeWheelStartConfig = timeWheelStartConfig;
    }
    @Bean
    public PersistenceFactory persistenceFactory(){
        return new PersistenceFactory(timeWheelStartConfig);
    }
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE - 1)
    @ConditionalOnProperty(prefix = "eternal.clock", value = "enabled", havingValue = "true")
    public TimeWheel timeWheel(){
        return new TimeWheel(false,timeWheelStartConfig,persistenceFactory());
    }

    @Bean(name = "eclock-pool")
    @ConditionalOnProperty(prefix = "eternal.clock", value = "enabled", havingValue = "true")
    public ThreadPoolTaskExecutor wheelPool(){
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
        PrintBanner.print(timeWheelStartConfig.getVersion());
        final TimeWheelStartHandler timeWheelStartHandler = new TimeWheelStartHandler(timeWheel(),wheelPool(),timeWheelStartConfig);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new SynchronousQueue<>(),
            r -> {
                Thread t= new Thread(r);
                // if you want debug current project please set Daemon false
                t.setDaemon(true);
                t.setName("eclock");
                return t;
            });
        threadPoolExecutor.execute(timeWheelStartHandler);
        LOGGER.info("eternal-clock started ...");
        return timeWheelStartHandler;
    }
    @ConditionalOnProperty(prefix = "eternal.clock", value = "enabled", havingValue = "true")
    @Bean
    public ApplicationRunner applicationRunner(){
        return args -> {
            if(timeWheelStartConfig.getPersistence().isEnabled()){
                Persistence persistence = persistenceFactory().getPersistence(
                    timeWheelStartConfig.getPersistence().getTypeName());
                if(persistence == null) throw new PersistenceInstanceException("persistence instance exception");
                List<DataModel> dataModels = persistence.get();
                LOGGER.info("fix history task ...");
                final long appStartTime = System.currentTimeMillis();
                dataModels.parallelStream()
                    .filter(dataModel -> Strings.isNotBlank(dataModel.getClazz()))
                    .filter(dataModel -> !dataModel.isLoopTask())
                    .forEach(dataModel -> timeWheel().addAndFixTask(dataModel,appStartTime));
            }
        };

    }


}
