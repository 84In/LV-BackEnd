package com.luanvan.chatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.luanvan.chatservice", "com.luanvan.commonservice"})
public class ChatserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatserviceApplication.class, args);
    }

}
