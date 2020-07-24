package com.yu.eclock.core;

import com.yu.eclock.config.TimeWheelStartConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TimeWheelStartHandler implements Runnable{
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeWheelStartHandler.class) ;
    private final  ThreadPoolTaskExecutor taskExecutor;
    private final  TimeWheelStartConfig   timeWheelStartConfig;
    private final  TimeWheel   timeWheel;
    public TimeWheelStartHandler(TimeWheel timeWheel,ThreadPoolTaskExecutor taskExecutor, TimeWheelStartConfig timeWheelStartConfig){
        this.taskExecutor = taskExecutor;
        this.timeWheelStartConfig = timeWheelStartConfig;
        this.timeWheel = timeWheel;
    }
    public void debugTask(Set<AbstractTask<?>> taskBySlotPoint){
        taskBySlotPoint.forEach(task -> LOGGER.debug("exec slot task name {}",task.getTaskName()));
    }
    /**
     * start timeWheel task
     */
    public void exec(){
        while (timeWheelStartConfig.isEnabled()){
            int slotPoint = timeWheel.moveCurrentSlotPoint();
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("slot index = {} ", (slotPoint + 1));
            }
            Set<AbstractTask<?>> taskBySlotPoint = timeWheel.getTaskBySlotPoint();
            if(timeWheel.isLazyInit()){
                if(taskBySlotPoint != null){
                    if(LOGGER.isDebugEnabled()){
                        debugTask(taskBySlotPoint);
                    }
                    for (AbstractTask<?> abstractTask : taskBySlotPoint) {
                        taskExecutor.execute(abstractTask);
                    }
                }
            }else{
                if(LOGGER.isDebugEnabled()){
                    debugTask(taskBySlotPoint);
                }
                for (AbstractTask<?>  task : taskBySlotPoint) {
                    taskExecutor.execute(task);
                }
            }
            // clear(timeWheel,taskBySlotPoint);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * clear done task
     */
    void clear(TimeWheel timeWheel,Set<AbstractTask<?>> taskBySlotPoint){
        taskExecutor.execute(() -> {
            if(taskBySlotPoint != null && taskBySlotPoint.size() > 0){
                for (AbstractTask<?> task : taskBySlotPoint){
                    if(task.isDone()){
                        timeWheel.removeTask(timeWheel.getCurrentSlotPoint(),task);
                    }
                }
            }

        });
    }

    @Override
    public void run() {
        exec();
    }
}
