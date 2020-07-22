package com.yu.eclock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
public class CircularApplication {
    public static void main(String[] args) {
        SpringApplication.run(CircularApplication.class, args);
    }

}
