package com.lty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class kafka2hbaseApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(kafka2hbaseApplication.class, args);
    }
}
