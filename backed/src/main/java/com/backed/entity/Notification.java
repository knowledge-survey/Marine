package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("type")
    private String type;

    @TableField("priority")
    private String priority;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("related_id")
    private Long relatedId;

    @TableField("status")
    private String status;

    @TableField("sender_id")
    private Long senderId;

    @TableField("sender_name")
    private String senderName;

    @TableField("target_type")
    private String targetType;

    @TableField("target_role")
    private String targetRole;

    @TableField("target_user_id")
    private Long targetUserId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("read_time")
    private LocalDateTime readTime;
}