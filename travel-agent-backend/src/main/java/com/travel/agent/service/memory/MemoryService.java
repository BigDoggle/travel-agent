package com.travel.agent.service.memory;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.entity.UserPreference;

import java.util.List;

public interface MemoryService {
    /**
     * 生成对话摘要并提取关键信息
     */
    String generateConversationSummary(List<ChatMessage> messages);

    /**
     * 从对话中提取用户偏好
     */
    List<UserPreference> extractUserPreferences(List<ChatMessage> messages);

    /**
     * 保存对话摘要（覆盖模式）
     */
    void saveConversationSummary(Long userId, String sessionId, String summary);

    /**
     * 保存用户偏好
     */
    void saveUserPreference(Long userId, UserPreference preference);

    /**
     * 获取用户偏好
     */
    List<UserPreference> getUserPreferences(Long userId);

    /**
     * 获取最新的对话摘要
     */
    String getLatestConversationSummary(Long userId, String sessionId);

    /**
     * 构建记忆上下文（简化版）
     */
    String buildMemoryContext(Long userId, String sessionId, String agentType);
}