package com.luanvan.userservice.configure;

import com.luanvan.userservice.service.DataLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    @Bean
    ApplicationRunner addDefaultRoles(DataLoaderService dataLoaderService) {
        return args -> dataLoaderService.initDataRole();
    }

    @Bean
    ApplicationRunner addDefaultLocation(DataLoaderService dataLoaderService) {
        return args -> {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db/dataLocation.json");
            log.info(inputStream.toString());
            if (inputStream != null) {
                dataLoaderService.loadDataLocationFromJson(inputStream);
            } else {
                log.error("❌ File dataLocation.json not found");
            }
            log.info("✅ Data Viet Nam location is successfully initialized!");
        };
    }


}
