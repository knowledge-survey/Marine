package com.backed.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.backed.dto.NotificationWithReadStatusDTO;
import com.backed.entity.Notification;
import com.backed.entity.SystemConfig;
import com.backed.entity.User;
import com.backed.entity.UserNotificationRead;
import com.backed.service.NotificationService;
import com.backed.service.SystemConfigService;
import com.backed.service.UserNotificationReadService;
import com.backed.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private UserNotificationReadService userNotificationReadService;

    @GetMapping
    public Page<NotificationWithReadStatusDTO> list(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(required = false) String status,
                                                    @RequestParam(required = false) String type,
                                                    @RequestParam(required = false) Long userId) {
        LambdaQueryWrapper<Notification> wrapper = buildUserFilter(userId);
        applyNotificationFilters(wrapper);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Notification::getType, type);
        }
        wrapper.orderByDesc(Notification::getCreateTime);
        
        Page<Notification> notificationPage = notificationService.page(new Page<>(page, size), wrapper);
        
        // 转换为带阅读状态的DTO
        Page<NotificationWithReadStatusDTO> resultPage = new Page<>(page, size, notificationPage.getTotal());
        List<NotificationWithReadStatusDTO> dtoList = convertToDTOList(notificationPage.getRecords(), userId);
        resultPage.setRecords(dtoList);
        
        // 如果有status过滤，过滤结果
        if (status != null && !status.isEmpty()) {
            boolean onlyUnread = "UNREAD".equals(status);
            List<NotificationWithReadStatusDTO> filteredList = dtoList.stream()
                    .filter(dto -> onlyUnread ? !dto.getIsRead() : dto.getIsRead())
                    .collect(Collectors.toList());
            resultPage.setRecords(filteredList);
            resultPage.setTotal(filteredList.size());
        }
        
        return resultPage;
    }

    @GetMapping("/all")
    public List<NotificationWithReadStatusDTO> listAll(@RequestParam(required = false) Long userId) {
        LambdaQueryWrapper<Notification> wrapper = buildUserFilter(userId);
        applyNotificationFilters(wrapper);
        wrapper.orderByDesc(Notification::getCreateTime);
        List<Notification> notifications = notificationService.list(wrapper);
        return convertToDTOList(notifications, userId);
    }

    @GetMapping("/unread")
    public List<NotificationWithReadStatusDTO> getUnread(@RequestParam(required = false) Long userId) {
        List<NotificationWithReadStatusDTO> allNotifications = listAll(userId);
        return allNotifications.stream()
                .filter(dto -> !dto.getIsRead())
                .collect(Collectors.toList());
    }

    @GetMapping("/unread-count")
    public Map<String, Object> getUnreadCount(@RequestParam(required = false) Long userId) {
        List<NotificationWithReadStatusDTO> allNotifications = listAll(userId);
        long count = allNotifications.stream()
                .filter(dto -> !dto.getIsRead())
                .count();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("count", count);
        return result;
    }

    @GetMapping("/{id}")
    public Notification getById(@PathVariable Long id) {
        return notificationService.getById(id);
    }

    @PostMapping
    public Notification save(@RequestBody Notification notification) {
        notification.setCreateTime(LocalDateTime.now());
        if (notification.getTargetType() == null) {
            notification.setTargetType("ALL");
        }
        notificationService.save(notification);
        return notification;
    }

    @PutMapping
    public boolean updateById(@RequestBody Notification notification) {
        return notificationService.updateById(notification);
    }

    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        // 同时删除用户阅读记录
        LambdaQueryWrapper<UserNotificationRead> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserNotificationRead::getNotificationId, id);
        userNotificationReadService.remove(deleteWrapper);
        
        return notificationService.removeById(id);
    }

    @PutMapping("/{id}/read")
    public boolean markAsRead(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        if (userId == null) {
            return false;
        }
        
        // 检查是否已经标记为已读
        LambdaQueryWrapper<UserNotificationRead> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(UserNotificationRead::getUserId, userId)
                   .eq(UserNotificationRead::getNotificationId, id);
        if (userNotificationReadService.count(checkWrapper) > 0) {
            return true;
        }
        
        UserNotificationRead readRecord = new UserNotificationRead();
        readRecord.setUserId(userId);
        readRecord.setNotificationId(id);
        readRecord.setReadTime(LocalDateTime.now());
        return userNotificationReadService.save(readRecord);
    }

    @PutMapping("/mark-all-read")
    public boolean markAllAsRead(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return false;
        }
        
        // 获取用户的所有未读通知
        List<NotificationWithReadStatusDTO> unreadNotifications = getUnread(userId);
        
        for (NotificationWithReadStatusDTO dto : unreadNotifications) {
            UserNotificationRead readRecord = new UserNotificationRead();
            readRecord.setUserId(userId);
            readRecord.setNotificationId(dto.getId());
            readRecord.setReadTime(LocalDateTime.now());
            userNotificationReadService.save(readRecord);
        }
        
        return true;
    }
    
    private List<NotificationWithReadStatusDTO> convertToDTOList(List<Notification> notifications, Long userId) {
        if (notifications == null || notifications.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取该用户的所有阅读记录
        List<Long> notificationIds = notifications.stream()
                .map(Notification::getId)
                .collect(Collectors.toList());
        
        Map<Long, UserNotificationRead> readMap;
        if (userId != null && !notificationIds.isEmpty()) {
            LambdaQueryWrapper<UserNotificationRead> readWrapper = new LambdaQueryWrapper<>();
            readWrapper.eq(UserNotificationRead::getUserId, userId)
                      .in(UserNotificationRead::getNotificationId, notificationIds);
            List<UserNotificationRead> readList = userNotificationReadService.list(readWrapper);
            readMap = readList.stream()
                    .collect(Collectors.toMap(UserNotificationRead::getNotificationId, r -> r));
        } else {
            readMap = new java.util.HashMap<>();
        }

        // 转换为DTO
        return notifications.stream()
                .map(notification -> {
                    NotificationWithReadStatusDTO dto = new NotificationWithReadStatusDTO();
                    BeanUtils.copyProperties(notification, dto);
                    
                    UserNotificationRead readRecord = readMap.get(notification.getId());
                    dto.setIsRead(readRecord != null);
                    dto.setReadTime(readRecord != null ? readRecord.getReadTime() : null);
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<Notification> buildUserFilter(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        if (userId == null) {
            return wrapper;
        }
        User user = userService.findById(userId).orElse(null);
        if (user == null) {
            return wrapper;
        }
        String role = user.getRole() != null ? user.getRole().name() : "BREEDER";
        if ("SUPER_ADMIN".equals(role)) {
            return wrapper;
        }
        wrapper.and(w -> {
            w.eq(Notification::getTargetType, "ALL").or()
             .eq(Notification::getSenderId, userId).or()
             .eq(Notification::getTargetUserId, userId).or()
             .eq(Notification::getTargetType, "ROLE")
             .eq(Notification::getTargetRole, role);
        });
        return wrapper;
    }
    
    private void applyNotificationFilters(LambdaQueryWrapper<Notification> wrapper) {
        // 检查水质告警开关
        boolean waterAlarmEnabled = getConfigBoolean("enable_water_alarm", true);
        // 检查库存预警开关
        boolean inventoryAlarmEnabled = getConfigBoolean("enable_inventory_alarm", true);
        // 检查病害提醒开关
        boolean diseaseAlarmEnabled = getConfigBoolean("enable_disease_alarm", true);
        
        // 构建过滤条件：如果某个开关关闭，则排除该类型的通知
        wrapper.and(w -> {
            // 始终显示INFO和REMINDER类型
            w.eq(Notification::getType, "INFO").or().eq(Notification::getType, "REMINDER");
            
            // 只有当开关启用时才显示对应的告警类型
            if (waterAlarmEnabled) {
                w.or().eq(Notification::getType, "WARNING");
            }
            if (inventoryAlarmEnabled) {
                w.or().eq(Notification::getType, "INV_ALARM");
            }
            if (diseaseAlarmEnabled) {
                w.or().eq(Notification::getType, "DISEASE_ALARM");
            }
        });
    }
    
    private boolean getConfigBoolean(String key, boolean defaultValue) {
        SystemConfig config = systemConfigService.getOne(
            new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key)
        );
        if (config != null && config.getConfigValue() != null) {
            return Boolean.parseBoolean(config.getConfigValue());
        }
        return defaultValue;
    }
}