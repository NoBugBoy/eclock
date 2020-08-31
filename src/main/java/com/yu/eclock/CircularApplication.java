package com.yu.eclock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.LinkedList;
import java.util.List;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, RedisAutoConfiguration.class})
public class CircularApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(CircularApplication.class, args);
    }

}
