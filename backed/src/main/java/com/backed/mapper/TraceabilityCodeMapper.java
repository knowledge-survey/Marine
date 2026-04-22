package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.TraceabilityCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface TraceabilityCodeMapper extends BaseMapper<TraceabilityCode> {

    @Select("SELECT * FROM traceability_code WHERE code = #{code}")
    Optional<TraceabilityCode> findByCode(@Param("code") String code);

    @Select("SELECT * FROM traceability_code WHERE harvest_id = #{harvestId}")
    Optional<TraceabilityCode> findByHarvestId(@Param("harvestId") Long harvestId);

    @Select("SELECT * FROM traceability_code WHERE batch_no = #{batchNo}")
    Optional<TraceabilityCode> findByBatchNo(@Param("batchNo") String batchNo);
}
