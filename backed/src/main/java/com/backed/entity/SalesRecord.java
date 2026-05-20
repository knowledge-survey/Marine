package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sales_record")
public class SalesRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("harvest_id")
    private Long harvestId;

    @TableField("product_name")
    private String productName;

    @TableField("species")
    private String species;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("unit")
    private String unit;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("customer_name")
    private String customerName;

    @TableField("customer_phone")
    private String customerPhone;

    @TableField("sale_date")
    private LocalDate saleDate;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("status")
    private String status;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
