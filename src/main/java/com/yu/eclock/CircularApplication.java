package com.yu.eclock;

import com.yu.eclock.core.TimeWheel;
import com.yu.eclock.test.Task1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, RedisAutoConfiguration.class})
public class CircularApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(CircularApplication.class, args);
        TimeWheel bean = run.getBean(TimeWheel.class);
        for (int i = 0; i < 10; i++) {
            Task1 task = new Task1(bean,"test"+i,10+(i * 10));
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, i);
            task.setTaskData(list);
            task.joinTimeWheel();
        }
    }

}
