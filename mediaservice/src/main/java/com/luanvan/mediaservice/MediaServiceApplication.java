package com.luanvan.mediaservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.luanvan.mediaservice", "com.luanvan.commonservice"})
public class MediaServiceApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        System.setProperty("cloudinary.cloudName", dotenv.get("CLOUDINARY_CLOUD_NAME"));
        System.setProperty("cloudinary.apiKey", dotenv.get("CLOUDINARY_API_KEY"));
        System.setProperty("cloudinary.apiSecret", dotenv.get("CLOUDINARY_API_SECRET"));
        System.setProperty("cloudinary.uploadAssetsName", dotenv.get("CLOUDINARY_UPLOAD_ASSETS_NAME"));
        SpringApplication.run(MediaServiceApplication.class, args);
    }

}
