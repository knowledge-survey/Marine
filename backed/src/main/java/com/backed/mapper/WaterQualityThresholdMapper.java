package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.WaterQualityThreshold;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface WaterQualityThresholdMapper extends BaseMapper<WaterQualityThreshold> {

    @Select("SELECT * FROM water_quality_threshold WHERE zone_id = #{zoneId}")
    Optional<WaterQualityThreshold> findByZoneId(@Param("zoneId") Long zoneId);
}
