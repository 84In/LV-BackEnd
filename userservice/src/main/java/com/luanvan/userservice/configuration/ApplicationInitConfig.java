package com.luanvan.userservice.configuration;

import com.luanvan.userservice.command.model.UserCreateModel;
import com.luanvan.userservice.command.service.UserCommandService;
import com.luanvan.userservice.repository.RoleRepository;
import com.luanvan.userservice.repository.UserRepository;
import com.luanvan.userservice.service.DataLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    @Bean
    @Order(1)
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
    @Order(2)
    ApplicationRunner addDefaultUsers(UserCommandService userCommandService, RoleRepository roleRepository,
            UserRepository userRepository) {
        return args -> {
            try {

                log.info("đang thực hiện tạo người dùng quản trị");
                if (!roleRepository.existsById("admin")) {
                    throw new RuntimeException("Role admin chưa được tạo thành");
                }
                if (userRepository.existsByUsername("admin")) {
                    log.info("Tài khoản quản trị đã khởi tạo sẵn");
                    throw new RuntimeException("Tài khoản đã khởi tạo");
                }
                UserCreateModel userCreateModel = new UserCreateModel(
                        "admin",
                        "admin123",
                        "Vanoushop@gmail.com",
                        "admin",
                        "admin",
                        "admin");

                var result = userCommandService.save(userCreateModel);
                log.info(result.toString());
                log.info("Tài khoản admin đã được khởi tạo với mật khầu: {}", userCreateModel.getPassword());

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        };
    }

}
