package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("seedling_inventory")
public class SeedlingInventory {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("species")
    private String seedlingName;

    @TableField("seedling_type")
    private String seedlingType;

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

    @TableField("hatch_date")
    private LocalDate hatchDate;

    @TableField("price")
    private BigDecimal unitPrice;

    @TableField("min_threshold")
    private BigDecimal warningThreshold;

    @TableField("batch_no")
    private String batchNo;

    @TableField("status")
    private String status;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
