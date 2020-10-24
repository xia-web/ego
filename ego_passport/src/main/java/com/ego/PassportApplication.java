package com.ego;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PassportApplication {
    public static void main(String[] args) {
        SpringApplication.run(PassportApplication.class,args);
    }
}
