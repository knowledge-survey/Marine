package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.DiseaseRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DiseaseRecordMapper extends BaseMapper<DiseaseRecord> {

    @Select("SELECT * FROM disease_record WHERE zone_id = #{zoneId} ORDER BY occurrence_date DESC")
    List<DiseaseRecord> findByZoneIdOrderByOccurrenceDateDesc(@Param("zoneId") Long zoneId);

    @Select("SELECT * FROM disease_record WHERE status = #{status}")
    List<DiseaseRecord> findByStatus(@Param("status") DiseaseRecord.DiseaseStatus status);
}
