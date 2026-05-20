package com.backed.service;

import com.backed.entity.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Component
public class OperationLogHelper {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 记录操作日志 - 从请求头获取当前用户信息
     */
    public void log(String module, String operation) {
        log(module, operation, null, null);
    }

    /**
     * 记录操作日志 - 从请求头获取当前用户信息，带方法和参数
     */
    public void log(String module, String operation, String method, String params) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            
            HttpServletRequest request = attributes.getRequest();
            
            // 从请求头获取用户信息
            String userIdStr = request.getHeader("X-User-Id");
            String username = request.getHeader("X-Username");
            
            Long userId = null;
            if (userIdStr != null && !userIdStr.isEmpty()) {
                try {
                    userId = Long.parseLong(userIdStr);
                } catch (NumberFormatException e) {
                    // 忽略
                }
            }
            
            // 获取 IP 地址
            String ipAddress = getClientIpAddress(request);
            
            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setUsername(username != null ? username : "unknown");
            log.setModule(module);
            log.setOperation(operation);
            log.setMethod(method != null ? method : request.getMethod());
            log.setParams(params != null ? params : "-");
            log.setIpAddress(ipAddress);
            log.setStatus("SUCCESS");
            log.setCreateTime(LocalDateTime.now());
            operationLogService.save(log);
        } catch (Exception e) {
            // fail silently - logging should not break business logic
            e.printStackTrace();
        }
    }

    /**
     * 记录操作日志 - 手动指定用户信息
     */
    public void log(Long userId, String username, String module, String operation) {
        log(userId, username, module, operation, "-", "-");
    }

    /**
     * 记录操作日志 - 手动指定用户信息，带方法和参数
     */
    public void log(Long userId, String username, String module, String operation, String method, String params) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String ipAddress = null;
            if (attributes != null) {
                ipAddress = getClientIpAddress(attributes.getRequest());
            }
            
            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setModule(module);
            log.setOperation(operation);
            log.setMethod(method);
            log.setParams(params);
            log.setIpAddress(ipAddress);
            log.setStatus("SUCCESS");
            log.setCreateTime(LocalDateTime.now());
            operationLogService.save(log);
        } catch (Exception e) {
            // fail silently - logging should not break business logic
        }
    }
    
    /**
     * 获取客户端 IP 地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多个代理，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}