package com.luanvan.userservice.configuration;

import com.luanvan.userservice.command.command.CreateUserCommand;
import com.luanvan.userservice.command.model.UserCreateModel;
import com.luanvan.userservice.command.service.UserCommandService;
import com.luanvan.userservice.repository.UserRepository;
import com.luanvan.userservice.service.DataLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.UUID;

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

    @Bean
    ApplicationRunner addDefaultUsers(UserCommandService userCommandService) {
        return args -> {

         try {
             UserCreateModel userCreateModel = new UserCreateModel(
                     "admin",
                     "admin123",
                     "Vanoushop@gmail.com",
                     "admin",
                     "admin",
                     "admin"
             );

             var result =  userCommandService.save(userCreateModel);
             log.info(result.toString());
             log.info("Tài khoản admin đã được khởi tạo với mật khầu: {}",userCreateModel.getPassword());
         } catch (Exception e) {
             log.error(e.getMessage());
         }
        };
    }


}
