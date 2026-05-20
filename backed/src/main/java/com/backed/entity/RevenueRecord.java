package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("revenue_record")
public class RevenueRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("batch_no")
    private String batchNo;

    @TableField("revenue_type")
    private String revenueType;

    @TableField("revenue_name")
    private String revenueName;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("record_date")
    private LocalDate recordDate;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
