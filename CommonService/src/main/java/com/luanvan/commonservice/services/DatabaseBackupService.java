package com.luanvan.commonservice.services;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DatabaseBackupService {
    @Value("${mysql.backup-root-dir}")
    private String backupRootDir;

    public void backupDatabase(String serviceName, String databaseName, String user, String password) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String serviceDir = backupRootDir + serviceName + "/";
        String backupFile = serviceDir + "backup_" + timestamp + ".sql";

        // Tạo thư mục nếu chưa tồn tại
        new File(serviceDir).mkdirs();

        // Lệnh backup MySQL
        String command = String.format("mysqldump -u%s -p%s %s > %s", user, password, databaseName, backupFile);

        try {
            Process process = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("✅ Backup thành công: {}", backupFile);
            } else {
                log.error("❌ Lỗi khi backup database: {}", databaseName);
            }
        } catch (IOException | InterruptedException e) {
            log.error("❌ Lỗi khi thực thi backup", e);
        }
    }
}
