package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.WaterQuality;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface WaterQualityMapper extends BaseMapper<WaterQuality> {

    @Select("SELECT * FROM water_quality WHERE zone_id = #{zoneId} ORDER BY record_time DESC")
    List<WaterQuality> findByZoneIdOrderByRecordTimeDesc(@Param("zoneId") Long zoneId);

    @Select("SELECT * FROM water_quality WHERE zone_id = #{zoneId} AND record_time BETWEEN #{startTime} AND #{endTime} ORDER BY record_time ASC")
    List<WaterQuality> findByZoneIdAndRecordTimeBetweenOrderByRecordTimeAsc(
            @Param("zoneId") Long zoneId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM water_quality WHERE zone_id = #{zoneId} ORDER BY record_time DESC LIMIT 1")
    WaterQuality findFirstByZoneIdOrderByRecordTimeDesc(@Param("zoneId") Long zoneId);

    @Select("SELECT * FROM water_quality WHERE is_warning = 1 ORDER BY record_time DESC")
    List<WaterQuality> findByIsWarningTrueOrderByRecordTimeDesc();
}
