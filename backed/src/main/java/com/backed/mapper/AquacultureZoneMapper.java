package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.AquacultureZone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AquacultureZoneMapper extends BaseMapper<AquacultureZone> {

    @Select("SELECT * FROM aquaculture_zone WHERE parent_id IS NULL")
    List<AquacultureZone> findByParentIsNull();

    @Select("SELECT * FROM aquaculture_zone WHERE parent_id = #{parentId}")
    List<AquacultureZone> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM aquaculture_zone WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<AquacultureZone> findByNameContaining(@Param("name") String name);
}
