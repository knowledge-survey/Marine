package com.backed.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("water_quality_simulation")
public class WaterQualitySimulation {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("zone_id")
    private Long zoneId;

    @TableField("temp_min")
    private BigDecimal tempMin;

    @TableField("temp_max")
    private BigDecimal tempMax;

    @TableField("do_min")
    private BigDecimal doMin;

    @TableField("do_max")
    private BigDecimal doMax;

    @TableField("ph_min")
    private BigDecimal phMin;

    @TableField("ph_max")
    private BigDecimal phMax;

    @TableField("salinity_min")
    private BigDecimal salinityMin;

    @TableField("salinity_max")
    private BigDecimal salinityMax;
}
