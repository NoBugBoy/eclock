package com.yu.eclock;

import com.yu.eclock.core.TimeWheel;
import com.yu.eclock.test.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CircularApplication {
    @Autowired
    private TimeWheel timeWheel;
    public static void main(String[] args) {
        SpringApplication.run(CircularApplication.class, args);

    }
    @Bean
    public Task task(){
        Task task = new Task(timeWheel,"123",100);
        task.setStartedUp(true);
        task.setLoopCount(-1);
        task.setTaskData("12312312");
        return task;
    }

}
