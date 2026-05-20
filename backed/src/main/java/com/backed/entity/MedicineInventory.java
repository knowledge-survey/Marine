package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("medicine_inventory")
public class MedicineInventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("medicine_name")
    private String medicineName;

    @TableField("medicine_type")
    private String type;

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
    private LocalDate manufactureDate;

    @TableField("expiry_date")
    private LocalDate expireDate;

    @TableField("price")
    private BigDecimal unitPrice;

    @TableField("min_threshold")
    private BigDecimal warningThreshold;

    @TableField("usage_description")
    private String usageDescription;

    @TableField("status")
    private String status;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
