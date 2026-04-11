package com.travel.agent.mapper;

import com.travel.agent.entity.UserPreference;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserPreferenceMapper {

    @Insert("INSERT INTO user_preference (user_id, preference_type, preference_value, description, confidence, source, created_at, updated_at) " +
            "VALUES (#{userId}, #{preferenceType}, #{preferenceValue}, #{description}, #{confidence}, #{source}, #{createdAt}, #{updatedAt})")
    int insert(UserPreference userPreference);

    @Update("UPDATE user_preference SET preference_value = #{preferenceValue}, description = #{description}, " +
            "confidence = #{confidence}, source = #{source}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(UserPreference userPreference);

    @Select("SELECT * FROM user_preference WHERE user_id = #{userId} ORDER BY confidence DESC, updated_at DESC")
    List<UserPreference> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM user_preference WHERE user_id = #{userId} AND preference_type = #{preferenceType} LIMIT 1")
    UserPreference findByUserIdAndType(@Param("userId") Long userId, @Param("preferenceType") String preferenceType);
}