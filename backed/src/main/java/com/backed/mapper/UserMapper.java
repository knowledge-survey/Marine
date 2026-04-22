package com.backed.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backed.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    Optional<User> findByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);
}
