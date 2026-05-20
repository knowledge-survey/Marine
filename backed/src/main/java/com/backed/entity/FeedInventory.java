package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("feed_inventory")
public class FeedInventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("feed_name")
    private String feedName;

    @TableField("brand")
    private String brand;

    @TableField("feed_type")
    private String feedType;

    @TableField("specification")
    private String specification;

    @TableField("quantity")
    private BigDecimal currentStock;

    @TableField("unit")
    private String unit;

    @TableField("supplier")
    private String supplier;

    @TableField("zone_name")
    private String zoneName;

    @TableField("purchase_date")
    private LocalDate purchaseDate;

    @TableField("expiry_date")
    private LocalDate expireDate;

    @TableField("price")
    private BigDecimal costPerKg;

    @TableField("min_threshold")
    private BigDecimal warningThreshold;

    @TableField("status")
    private String status;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
