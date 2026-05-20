package com.backed.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationWithReadStatusDTO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String priority;
    private Long zoneId;
    private Long relatedId;
    private Long senderId;
    private String senderName;
    private String targetType;
    private String targetRole;
    private Long targetUserId;
    private LocalDateTime createTime;
    
    // 阅读状态相关字段
    private Boolean isRead;
    private LocalDateTime readTime;
}
