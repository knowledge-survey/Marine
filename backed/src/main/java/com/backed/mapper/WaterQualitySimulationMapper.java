package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.WaterQualitySimulation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface WaterQualitySimulationMapper extends BaseMapper<WaterQualitySimulation> {

    @Select("SELECT * FROM water_quality_simulation WHERE zone_id = #{zoneId}")
    Optional<WaterQualitySimulation> findByZoneId(@Param("zoneId") Long zoneId);
}
