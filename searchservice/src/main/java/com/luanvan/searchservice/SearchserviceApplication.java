package com.luanvan.searchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.luanvan.searchservice.*", "com.luanvan.commonservice"})
public class SearchserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchserviceApplication.class, args);
    }
}
