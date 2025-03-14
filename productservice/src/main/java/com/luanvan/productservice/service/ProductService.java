package com.luanvan.productservice.service;

import com.luanvan.commonservice.services.DatabaseBackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProductService {
    @Value("${spring.datasource.username}")
    private String dbUser;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    private String dbName = "productservicedb";
    @Autowired
    private DatabaseBackupService databaseBackupService;

    @Scheduled(cron = "0 0 0 */7 * ?") // Chạy mỗi 7 ngày lúc 00:00
    public void backupDatabase() {
        log.info("🔹 Backup Product Database...");
        databaseBackupService.backupDatabase(dbName, dbName, dbUser, dbPassword);
    }
}
