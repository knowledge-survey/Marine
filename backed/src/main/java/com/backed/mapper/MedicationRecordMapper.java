package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.MedicationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicationRecordMapper extends BaseMapper<MedicationRecord> {

    @Select("SELECT * FROM medication_record WHERE disease_id = #{diseaseId} ORDER BY medication_date DESC")
    List<MedicationRecord> findByDiseaseIdOrderByMedicationDateDesc(@Param("diseaseId") Long diseaseId);
}
