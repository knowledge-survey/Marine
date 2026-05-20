package com.backed.controller;

import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private static final String DB_PATH = "./marine_aquaculture.db";
    private static final String BACKUP_DIR = "./backups";

    private File getBackupDir() {
        File dir = new File(BACKUP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    @PostMapping("/create")
    public Map<String, Object> createBackup() {
        Map<String, Object> result = new HashMap<>();
        try {
            File dbFile = new File(DB_PATH);
            if (!dbFile.exists()) {
                result.put("success", false);
                result.put("message", "数据库文件不存在");
                return result;
            }
            File backupDir = getBackupDir();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupName = "backup_" + timestamp + ".db";
            File backupFile = new File(backupDir, backupName);
            Files.copy(dbFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            long sizeKb = backupFile.length() / 1024;
            result.put("success", true);
            result.put("message", "备份创建成功");
            result.put("backupName", backupName);
            result.put("size", sizeKb + " KB");
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "备份失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> listBackups() {
        Map<String, Object> result = new HashMap<>();
        File backupDir = getBackupDir();
        File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".db"));
        List<Map<String, Object>> backups = new ArrayList<>();
        if (files != null) {
            Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
            for (File file : files) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("backupName", file.getName());
                item.put("backupSize", (file.length() / 1024) + " KB");
                item.put("createTime", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified())));
                backups.add(item);
            }
        }
        result.put("success", true);
        result.put("backups", backups);
        return result;
    }

    @PostMapping("/restore/{backupName}")
    public Map<String, Object> restoreBackup(@PathVariable String backupName) {
        Map<String, Object> result = new HashMap<>();
        try {
            File backupDir = getBackupDir();
            File backupFile = new File(backupDir, backupName);
            if (!backupFile.exists()) {
                result.put("success", false);
                result.put("message", "备份文件不存在");
                return result;
            }
            File dbFile = new File(DB_PATH);
            if (dbFile.exists()) {
                String safetyBackup = "restore_safety_" + System.currentTimeMillis() + ".db";
                Files.copy(dbFile.toPath(), new File(backupDir, safetyBackup).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            Files.copy(backupFile.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            result.put("success", true);
            result.put("message", "恢复成功，原数据库已安全备份");
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "恢复失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/{backupName}")
    public Map<String, Object> deleteBackup(@PathVariable String backupName) {
        Map<String, Object> result = new HashMap<>();
        File backupDir = getBackupDir();
        File backupFile = new File(backupDir, backupName);
        if (backupFile.exists() && backupFile.delete()) {
            result.put("success", true);
            result.put("message", "备份删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败");
        }
        return result;
    }
}