package com.backed.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("water_quality")
public class WaterQuality {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("temperature")
    private BigDecimal temperature;

    @TableField("dissolved_oxygen")
    private BigDecimal dissolvedOxygen;

    @TableField("ph")
    private BigDecimal ph;

    @TableField("salinity")
    private BigDecimal salinity;

    @TableField("is_warning")
    private Boolean isWarning = false;

    @TableField("warning_info")
    private String warningInfo;

    @TableField(value = "record_time", fill = FieldFill.INSERT)
    private LocalDateTime recordTime;
}
