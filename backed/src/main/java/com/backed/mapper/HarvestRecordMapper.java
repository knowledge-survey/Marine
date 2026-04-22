package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.HarvestRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HarvestRecordMapper extends BaseMapper<HarvestRecord> {

    @Select("SELECT * FROM harvest_record WHERE zone_id = #{zoneId} ORDER BY harvest_date DESC")
    List<HarvestRecord> findByZoneIdOrderByHarvestDateDesc(@Param("zoneId") Long zoneId);
}
