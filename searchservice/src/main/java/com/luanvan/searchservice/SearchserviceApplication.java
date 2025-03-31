package com.luanvan.searchservice;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.luanvan.searchservice", "com.luanvan.commonservice"})
@EnableElasticsearchRepositories(basePackages = "com.luanvan.searchservice.repository")
public class SearchserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchserviceApplication.class, args);
    }
}
