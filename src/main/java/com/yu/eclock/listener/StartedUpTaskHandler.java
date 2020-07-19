package com.yu.eclock.listener;

import com.yu.eclock.config.TimeWheelStartConfig;
import com.yu.eclock.core.AbstractTask;
import com.yu.eclock.core.TimeWheel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;


public class StartedUpTaskHandler implements ApplicationListener<ApplicationStartedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartedUpTaskHandler.class);
    private final TimeWheel timeWheel;
    private final TimeWheelStartConfig timeWheelStartConfig;

    public StartedUpTaskHandler(TimeWheel timeWheel, TimeWheelStartConfig timeWheelStartConfig) {
        this.timeWheel = timeWheel;
        this.timeWheelStartConfig = timeWheelStartConfig;
    }



    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        String[] beanNamesForType = applicationStartedEvent.getApplicationContext().getBeanNamesForType(
            AbstractTask.class);
        if(timeWheelStartConfig.isEnabled()){
            if(beanNamesForType.length > 0){
                LOGGER.info("The automatic tasks:");
                for (String s : beanNamesForType) {
                    Object bean = applicationStartedEvent.getApplicationContext().getBean(s);
                    AbstractTask task = (AbstractTask)bean;
                    if(task.isStartedUp()){
                        task.joinTimeWheel();
                    }
                    LOGGER.info("| task name : {} |",task.getTaskName());
                    LOGGER.info("| cycle time : {} s |",task.getSeconds());
                    LOGGER.info("--------------------");
                }
            }
        }


    }
}
