package com.luv2code.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MrCandyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MrCandyAppApplication.class, args);
    }

}
