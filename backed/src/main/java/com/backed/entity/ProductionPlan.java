package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("production_plan")
public class ProductionPlan {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("zone_name")
    private String zoneName;

    @TableField("plan_name")
    private String planName;

    @TableField("species")
    private String species;

    @TableField("batch_no")
    private String batchNo;

    @TableField("plan_type")
    private String planType;

    @TableField("start_date")
    private LocalDate startDate;

    @TableField("end_date")
    private LocalDate endDate;

    @TableField("target_quantity")
    private BigDecimal targetQuantity;

    @TableField("target_size")
    private String targetSize;

    @TableField("status")
    private String status;

    @TableField("description")
    private String description;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
