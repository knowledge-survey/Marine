package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    @Select("SELECT * FROM operation_log WHERE username = #{username} ORDER BY create_time DESC")
    List<OperationLog> findByUsernameOrderByCreateTimeDesc(@Param("username") String username);

    @Select("SELECT * FROM operation_log WHERE module = #{module} ORDER BY create_time DESC")
    List<OperationLog> findByModuleOrderByCreateTimeDesc(@Param("module") String module);
}
