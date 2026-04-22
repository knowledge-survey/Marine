package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("seedling_record")
public class SeedlingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("species")
    private String species;

    @TableField("batch_no")
    private String batchNo;

    @TableField("quantity")
    private BigDecimal quantity;

    @TableField("source")
    private String source;

    @TableField("supplier")
    private String supplier;

    @TableField("release_date")
    private LocalDate releaseDate;

    @TableField("remarks")
    private String remarks;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
