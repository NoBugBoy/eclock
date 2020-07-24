package com.yu.eclock;

import com.yu.eclock.core.TimeWheel;
import com.yu.eclock.test.Task1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class CircularApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(CircularApplication.class, args);
        TimeWheel bean = run.getBean(TimeWheel.class);
        for (int i = 0; i < 10; i++) {
            Task1 task = new Task1(bean,"test"+i,20+i);
            task.setTaskData("1231231");
            task.joinTimeWheel();
        }
    }

}
