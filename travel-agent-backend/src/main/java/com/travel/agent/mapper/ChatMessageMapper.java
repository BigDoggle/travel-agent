package com.travel.agent.mapper;

import com.travel.agent.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Insert("INSERT INTO chat_message (session_id, user_id, role, content, metadata, created_at) " +
            "VALUES (#{sessionId}, #{userId}, #{role}, #{content}, #{metadata}, #{createdAt})")
    int insert(ChatMessage chatMessage);

    @Select("SELECT * FROM chat_message WHERE user_id = #{userId} AND session_id = #{sessionId} ORDER BY created_at DESC LIMIT #{limit}")
    List<ChatMessage> findByUserAndSession(@Param("userId") Long userId, @Param("sessionId") String sessionId, @Param("limit") int limit);

    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId} ORDER BY created_at DESC")
    List<ChatMessage> findBySession(@Param("sessionId") String sessionId);

    @Select("SELECT * FROM chat_message WHERE user_id = #{userId} AND session_id = #{sessionId} ORDER BY created_at DESC LIMIT #{limit}")
    List<ChatMessage> findRecentByUserSession(@Param("userId") Long userId, @Param("sessionId") String sessionId, @Param("limit") int limit);
}