package com.travel.agent.mapper;

import com.travel.agent.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    User findByEmail(@Param("email") String email);

    int insert(User user);

    User findById(@Param("id") Long id);

    int update(User user);
}
