package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("username")
    private String username;

    @TableField("module")
    private String module;

    @TableField("operation")
    private String operation;

    @TableField("method")
    private String method;

    @TableField("params")
    private String params;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("status")
    private String status;

    @TableField("error_msg")
    private String errorMsg;

    @TableField("execute_time")
    private Integer executeTime;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
