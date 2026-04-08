package com.travel.agent.service.ai;

import com.travel.agent.entity.ChatMessage;
import reactor.core.publisher.Flux;
import java.util.List;

/**
 * AI对话服务接口
 */
public interface AIChatService extends AIService {
    
    /**
     * 处理用户输入，流式返回AI响应
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param message 用户输入消息
     * @return Flux<String> 流式返回的AI响应
     */
    Flux<String> processMessageStream(Long userId, String sessionId, String message);
    
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