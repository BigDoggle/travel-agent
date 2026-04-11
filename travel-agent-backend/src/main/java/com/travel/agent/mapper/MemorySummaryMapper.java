package com.travel.agent.mapper;

import com.travel.agent.entity.MemorySummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MemorySummaryMapper {

    @Insert("INSERT INTO memory_summary (user_id, session_id, summary_type, content, created_at, updated_at) " +
            "VALUES (#{userId}, #{sessionId}, #{summaryType}, #{content}, #{createdAt}, #{updatedAt})")
    int insert(MemorySummary memorySummary);

    @Update("UPDATE memory_summary SET content = #{content}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(MemorySummary memorySummary);

    @Select("SELECT * FROM memory_summary WHERE user_id = #{userId} AND session_id = #{sessionId} AND summary_type = #{summaryType} LIMIT 1")
    MemorySummary findByUserSessionAndType(@Param("userId") Long userId, @Param("sessionId") String sessionId, @Param("summaryType") String summaryType);

    @Select("SELECT * FROM memory_summary WHERE user_id = #{userId} ORDER BY updated_at DESC LIMIT 10")
    List<MemorySummary> findByUserId(@Param("userId") Long userId);
}