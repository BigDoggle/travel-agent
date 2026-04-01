package com.travel.agent.service.ai;

import com.travel.agent.entity.ChatMessage;
import java.util.List;

/**
 * 记忆服务接口
 */
public interface MemoryService extends AIService {
    
    /**
     * 存储短期记忆（Redis）
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param message 消息内容
     * @param isUser 是否为用户消息
     */
    void storeShortTermMemory(Long userId, String sessionId, String message, boolean isUser);
    
    /**
     * 获取短期记忆（Redis）
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 短期记忆列表
     */
    List<ChatMessage> getShortTermMemory(Long userId, String sessionId, int limit);
    
    /**
     * 清除短期记忆（Redis）
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    void clearShortTermMemory(Long userId, String sessionId);
    
    /**
     * 清除用户的所有短期记忆（Redis）
     * @param userId 用户ID
     */
    void clearAllShortTermMemory(Long userId);
    
    /**
     * 存储长期记忆（RAG）
     * @param userId 用户ID
     * @param content 内容
     * @param metadata 元数据
     */
    void storeLongTermMemory(Long userId, String content, String metadata);
    
    /**
     * 检索长期记忆（RAG）
     * @param userId 用户ID
     * @param query 查询内容
     * @param limit 限制数量
     * @return 相关记忆列表
     */
    List<String> retrieveLongTermMemory(Long userId, String query, int limit);
}
