package com.zbw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ZbwenApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZbwenApplication.class, args);
    }

}
