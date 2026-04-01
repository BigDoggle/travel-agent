package com.travel.agent.service.ai.impl;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.service.ai.AIChatService;
import com.travel.agent.service.ai.MCPService;
import com.travel.agent.service.ai.MemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * AI对话服务实现
 */
@Slf4j
@Service
public class AIChatServiceImpl implements AIChatService {

    @Autowired
    private MCPService mcpService;

    @Autowired
    private MemoryService memoryService;

    /**
     * 处理用户输入，流式返回AI响应
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param message 用户输入消息
     * @param consumer 回调函数，用于接收流式响应
     */
    @Override
    public void processMessageStream(Long userId, String sessionId, String message, Consumer<String> consumer) {
        log.info("处理用户消息（流式），用户ID: {}, 会话ID: {}, 消息: {}", userId, sessionId, message);
        
        // 存储用户消息到短期记忆
        memoryService.storeShortTermMemory(userId, sessionId, message, true);
        
        // 构建上下文信息（从短期记忆和长期记忆中获取）
        List<ChatMessage> shortTermMemory = memoryService.getShortTermMemory(userId, sessionId, 20);
        List<String> longTermMemory = memoryService.retrieveLongTermMemory(userId, message, 5);
        
        StringBuilder context = new StringBuilder();
        context.append("对话历史：\n");
        for (ChatMessage chatMessage : shortTermMemory) {
            context.append(chatMessage.getRole()).append(": ").append(chatMessage.getContent()).append("\n");
        }
        
        context.append("相关记忆：\n");
        for (String memory : longTermMemory) {
            context.append(memory).append("\n");
        }
        
        // 使用MCP协调多Agent执行任务
        String goal = message;
        String contextStr = context.toString();
        
        try {
            var results = mcpService.coordinateAgents(userId, sessionId, goal, contextStr);
            
            // 提取AI响应
            String response = (String) results.get("response");
            
            // 模拟流式返回
            String[] tokens = response.split(" ");
            for (String token : tokens) {
                consumer.accept(token + " ");
                // 模拟延迟，让流式效果更明显
                Thread.sleep(50);
            }
            
            // 存储AI响应到短期记忆
            memoryService.storeShortTermMemory(userId, sessionId, response, false);
            
            // 存储重要信息到长期记忆
            if (results.containsKey("importantInfo")) {
                String importantInfo = (String) results.get("importantInfo");
                memoryService.storeLongTermMemory(userId, importantInfo, "user_goal: " + goal);
            }
            
            log.info("流式返回完成，用户ID: {}, 会话ID: {}, 响应: {}", userId, sessionId, response);
        } catch (Exception e) {
            log.error("处理用户消息（流式）失败", e);
            consumer.accept("抱歉，处理您的请求时出现了错误，请稍后重试。");
        }
    }

    /**
     * 获取用户的对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 对话历史列表
     */
    @Override
    public List<ChatMessage> getChatHistory(Long userId, String sessionId, int limit) {
        log.info("获取对话历史，用户ID: {}, 会话ID: {}, 限制: {}", userId, sessionId, limit);
        return memoryService.getShortTermMemory(userId, sessionId, limit);
    }

    /**
     * 清除用户的对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     */
    @Override
    public void clearChatHistory(Long userId, String sessionId) {
        log.info("清除对话历史，用户ID: {}, 会话ID: {}", userId, sessionId);
        memoryService.clearShortTermMemory(userId, sessionId);
    }

    /**
     * 清除用户的所有对话历史
     * @param userId 用户ID
     */
    @Override
    public void clearAllChatHistory(Long userId) {
        log.info("清除用户的所有对话历史，用户ID: {}", userId);
        memoryService.clearAllShortTermMemory(userId);
    }
}