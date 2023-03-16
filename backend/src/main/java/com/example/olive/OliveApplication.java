package com.example.olive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@SpringBootApplication
@EnableMongoRepositories(basePackages = {"com.example.olive.repository"})
public class OliveApplication {

    public static void main(String[] args) {
        SpringApplication.run(OliveApplication.class, args);
    }

}
