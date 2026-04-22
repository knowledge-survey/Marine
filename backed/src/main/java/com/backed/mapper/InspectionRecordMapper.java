package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.InspectionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InspectionRecordMapper extends BaseMapper<InspectionRecord> {

    @Select("SELECT * FROM inspection_record WHERE zone_id = #{zoneId} ORDER BY inspection_time DESC")
    List<InspectionRecord> findByZoneIdOrderByInspectionTimeDesc(@Param("zoneId") Long zoneId);
}
