package com.github.feignclientutil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FeignClientUtilApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignClientUtilApplication.class, args);
    }

}
