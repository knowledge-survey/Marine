package com.backed.controller;

import com.backed.entity.SystemConfig;
import com.backed.entity.User;
import com.backed.service.OperationLogHelper;
import com.backed.service.SystemConfigService;
import com.backed.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private OperationLogHelper operationLogHelper;
    
    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User loginUser) {
        ResponseEntity<User> response = userService.findByUsername(loginUser.getUsername())
                .filter(user -> user.getPassword().equals(loginUser.getPassword()) && user.getEnabled())
                .map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.badRequest().build());
        
        // 记录登录日志
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            User user = response.getBody();
            operationLogHelper.log(user.getId(), user.getUsername(), "用户管理", "登录", "POST", "username=" + loginUser.getUsername());
        }
        
        return response;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        
        // 验证密码长度
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            int minLength = getMinPasswordLength();
            if (user.getPassword().length() < minLength) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        ResponseEntity<User> response = ResponseEntity.ok(userService.save(user));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("用户管理", "新增用户", "POST", "username=" + user.getUsername());
        }
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        user.setId(id);
        
        // 如果有密码修改，验证密码长度
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            int minLength = getMinPasswordLength();
            if (user.getPassword().length() < minLength) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        ResponseEntity<User> response = ResponseEntity.ok(userService.update(user));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("用户管理", "更新用户", "PUT", "id=" + id + ", username=" + user.getUsername());
        }
        return response;
    }
    
    private int getMinPasswordLength() {
        SystemConfig config = systemConfigService.getOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, "min_password_length")
        );
        if (config != null && config.getConfigValue() != null) {
            try {
                return Integer.parseInt(config.getConfigValue());
            } catch (NumberFormatException e) {
                return 6; // 默认值
            }
        }
        return 6; // 默认值
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        operationLogHelper.log("用户管理", "删除用户", "DELETE", "id=" + id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<User> toggleUserEnabled(@PathVariable Long id) {
        ResponseEntity<User> response = ResponseEntity.ok(userService.toggleEnabled(id));
        if (response.getStatusCode().is2xxSuccessful()) {
            operationLogHelper.log("用户管理", "切换用户状态", "PATCH", "id=" + id);
        }
        return response;
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<Map<String, String>> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            byte[] imageBytes = file.getBytes();
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "请上传图片文件"));
            }

            String formatType = contentType.replace("image/", "");
            if ("jpeg".equals(formatType)) formatType = "jpg";
            String base64Avatar = "data:image/" + formatType + ";base64," +
                    Base64.getEncoder().encodeToString(imageBytes);

            userService.updateAvatar(id, base64Avatar);

            return ResponseEntity.ok(Map.of("avatar", base64Avatar));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "上传失败"));
        }
    }

    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<Void> deleteAvatar(@PathVariable Long id) {
        userService.updateAvatar(id, null);
        return ResponseEntity.ok().build();
    }
}
