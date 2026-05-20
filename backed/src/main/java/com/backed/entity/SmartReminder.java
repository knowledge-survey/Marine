package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("smart_reminder")
public class SmartReminder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("reminder_type")
    private String reminderType;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("reminder_date")
    private LocalDate reminderDate;

    @TableField("reminder_time")
    private String reminderTime;

    @TableField("repeat_type")
    private String repeatType;

    @TableField("is_enabled")
    private Boolean isEnabled;

    @TableField("last_trigger_time")
    private LocalDateTime lastTriggerTime;

    @TableField("user_id")
    private Long userId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}