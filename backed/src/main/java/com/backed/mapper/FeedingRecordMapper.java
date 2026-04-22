package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.FeedingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedingRecordMapper extends BaseMapper<FeedingRecord> {

    @Select("SELECT * FROM feeding_record WHERE zone_id = #{zoneId} ORDER BY feeding_time DESC")
    List<FeedingRecord> findByZoneIdOrderByFeedingTimeDesc(@Param("zoneId") Long zoneId);
}
