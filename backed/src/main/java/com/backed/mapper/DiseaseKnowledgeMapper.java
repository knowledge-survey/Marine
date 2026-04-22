package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.DiseaseKnowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DiseaseKnowledgeMapper extends BaseMapper<DiseaseKnowledge> {

    @Select("SELECT * FROM disease_knowledge WHERE disease_name LIKE CONCAT('%', #{keyword}, '%')")
    List<DiseaseKnowledge> findByDiseaseNameContaining(@Param("keyword") String keyword);

    @Select("SELECT * FROM disease_knowledge WHERE symptoms LIKE CONCAT('%', #{keyword}, '%')")
    List<DiseaseKnowledge> findBySymptomsContaining(@Param("keyword") String keyword);
}
