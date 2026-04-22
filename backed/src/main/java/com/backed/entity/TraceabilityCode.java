package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("traceability_code")
public class TraceabilityCode {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("code")
    private String code;

    @TableField("harvest_id")
    private Long harvestId;

    @TableField("batch_no")
    private String batchNo;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("qr_code_url")
    private String qrCodeUrl;

    @TableField("description")
    private String description;

    @TableField("scan_count")
    private Integer scanCount = 0;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
