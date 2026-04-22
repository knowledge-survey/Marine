package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.SeedlingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SeedlingRecordMapper extends BaseMapper<SeedlingRecord> {

    @Select("SELECT * FROM seedling_record WHERE zone_id = #{zoneId} ORDER BY release_date DESC")
    List<SeedlingRecord> findByZoneIdOrderByReleaseDateDesc(@Param("zoneId") Long zoneId);

    @Select("SELECT * FROM seedling_record WHERE batch_no = #{batchNo} ORDER BY release_date DESC")
    List<SeedlingRecord> findByBatchNo(@Param("batchNo") String batchNo);

    @Select("SELECT DISTINCT batch_no FROM seedling_record WHERE batch_no IS NOT NULL")
    List<String> findAllBatchNos();
}
