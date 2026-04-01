package com.travel.agent.service.ai;

import com.travel.agent.entity.ChatMessage;
import java.util.List;
import java.util.function.Consumer;

/**
 * AI对话服务接口
 */
public interface AIChatService extends AIService {
    
    /**
     * 处理用户输入，流式返回AI响应
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param message 用户输入消息
     * @param consumer 回调函数，用于接收流式响应
     */
    void processMessageStream(Long userId, String sessionId, String message, Consumer<String> consumer);
    
    /**
     * 获取用户的对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 对话历史列表
     */
    List<ChatMessage> getChatHistory(Long userId, String sessionId, int limit);
    
    /**
     * 清除用户的对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    void clearChatHistory(Long userId, String sessionId);
    
    /**
     * 清除用户的所有对话历史
     * @param userId 用户ID
     */
    void clearAllChatHistory(Long userId);
}