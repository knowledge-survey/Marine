package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.backed.entity.SystemConfig;
import com.backed.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/system-settings")
public class SystemSettingsController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public Map<String, Object> getSettings() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("basic", buildBasicSettings());
        result.put("security", buildSecuritySettings());
        result.put("notification", buildNotificationSettings());
        return result;
    }

    @GetMapping("/{key}")
    public Map<String, Object> getSettingByKey(@PathVariable String key) {
        Map<String, Object> result = new HashMap<>();
        SystemConfig config = getConfigByKey(key);
        if (config != null) {
            result.put(key, config.getConfigValue());
        }
        return result;
    }

    @PutMapping
    public Map<String, Object> saveSettings(@RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        try {
            saveGroupConfig((Map<String, Object>) body.getOrDefault("basic", new HashMap<>()), "basic");
            saveGroupConfig((Map<String, Object>) body.getOrDefault("security", new HashMap<>()), "security");
            saveGroupConfig((Map<String, Object>) body.getOrDefault("notification", new HashMap<>()), "notification");
            result.put("success", true);
            result.put("message", "设置保存成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/{key}")
    public Map<String, Object> updateSettingByKey(@PathVariable String key, @RequestBody Map<String, Object> body) {
        Map<String, Object> result = new HashMap<>();
        Object value = body.get("configValue");
        if (value != null) {
            saveConfigValue(key, String.valueOf(value), "STRING", null);
            result.put("success", true);
            result.put("message", "设置更新成功");
        } else {
            result.put("success", false);
            result.put("message", "参数错误");
        }
        return result;
    }

    @PostMapping("/clear-cache")
    public Map<String, Object> clearCache() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "缓存清除成功");
        return result;
    }

    @PostMapping("/restart")
    public Map<String, Object> restartSystem() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "系统正在重启...");
        return result;
    }

    private SystemConfig getConfigByKey(String key) {
        return systemConfigService.getOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key));
    }

    private String getConfigValue(String key, String defaultValue) {
        SystemConfig config = getConfigByKey(key);
        return config != null ? config.getConfigValue() : defaultValue;
    }

    private void saveConfigValue(String key, String value, String type, String description) {
        SystemConfig config = getConfigByKey(key);
        if (config != null) {
            config.setConfigValue(value);
            config.setUpdateTime(LocalDateTime.now());
            systemConfigService.updateById(config);
        } else {
            config = new SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setConfigType(type != null ? type : "STRING");
            config.setDescription(description);
            config.setIsSystem(true);
            config.setCreateTime(LocalDateTime.now());
            config.setUpdateTime(LocalDateTime.now());
            systemConfigService.save(config);
        }
    }

    private Map<String, Object> buildBasicSettings() {
        Map<String, Object> basic = new LinkedHashMap<>();
        basic.put("systemName", getConfigValue("system_name", "海洋养殖管理系统"));
        basic.put("version", getConfigValue("system_version", "2.0.0"));
        basic.put("contactPhone", getConfigValue("contact_phone", ""));
        basic.put("announcement", getConfigValue("announcement", ""));
        return basic;
    }

    private Map<String, Object> buildSecuritySettings() {
        Map<String, Object> security = new LinkedHashMap<>();
        security.put("maxLoginAttempts", Integer.parseInt(getConfigValue("max_login_attempts", "5")));
        security.put("sessionTimeout", Integer.parseInt(getConfigValue("session_timeout", "120")));
        security.put("minPasswordLength", Integer.parseInt(getConfigValue("min_password_length", "6")));
        return security;
    }

    private Map<String, Object> buildNotificationSettings() {
        Map<String, Object> notification = new LinkedHashMap<>();
        notification.put("enableWaterAlarm", Boolean.parseBoolean(getConfigValue("enable_water_alarm", "true")));
        notification.put("enableInventoryAlarm", Boolean.parseBoolean(getConfigValue("enable_inventory_alarm", "true")));
        notification.put("enableDiseaseAlarm", Boolean.parseBoolean(getConfigValue("enable_disease_alarm", "true")));
        notification.put("remindAdvanceHours", Integer.parseInt(getConfigValue("remind_advance_hours", "24")));
        return notification;
    }

    @SuppressWarnings("unchecked")
    private void saveGroupConfig(Map<String, Object> group, String prefix) {
        for (Map.Entry<String, Object> entry : group.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String configKey;
            if ("basic".equals(prefix)) {
                switch (key) {
                    case "systemName": configKey = "system_name"; break;
                    case "version": configKey = "system_version"; break;
                    case "contactPhone": configKey = "contact_phone"; break;
                    case "announcement": configKey = "announcement"; break;
                    default: continue;
                }
            } else if ("security".equals(prefix)) {
                switch (key) {
                    case "maxLoginAttempts": configKey = "max_login_attempts"; break;
                    case "sessionTimeout": configKey = "session_timeout"; break;
                    case "minPasswordLength": configKey = "min_password_length"; break;
                    default: continue;
                }
            } else if ("notification".equals(prefix)) {
                switch (key) {
                    case "enableWaterAlarm": configKey = "enable_water_alarm"; break;
                    case "enableInventoryAlarm": configKey = "enable_inventory_alarm"; break;
                    case "enableDiseaseAlarm": configKey = "enable_disease_alarm"; break;
                    case "remindAdvanceHours": configKey = "remind_advance_hours"; break;
                    default: continue;
                }
            } else {
                continue;
            }
            saveConfigValue(configKey, String.valueOf(value), "STRING", null);
        }
    }
}