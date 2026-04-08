package com.travel.agent.service.ai.impl;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.service.ai.AIChatService;
import com.travel.agent.service.ai.MCPService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * AI 对话服务实现
 */
@Slf4j
@Service
public class AIChatServiceImpl implements AIChatService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.base-url}")
    private String openaiBaseUrl;

    @Value("${openai.model}")
    private String openaiModel;

    @Autowired
    private MCPService mcpService;

    // 简单的内存存储，用于模拟对话历史
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    /**
     * 处理用户输入，流式返回 AI 响应
     * @param userId 用户 ID
     * @param sessionId 会话 ID
     * @param message 用户输入消息
     * @return Flux<String> 流式返回的 AI 响应
     */
    @Override
    public Flux<String> processMessageStream(Long userId, String sessionId, String message) {
        log.info("处理用户消息（流式），用户 ID: {}, 会话 ID: {}, 消息：{}", userId, sessionId, message);
        
        return Flux.create(sink -> {
            StringBuilder fullResponse = new StringBuilder();
            
            try {
                // 1. 使用多智能体协调服务处理任务
                String result = mcpService.coordinateAgents(userId, sessionId, message);
                log.info("多智能体协调完成，用户 ID: {}, 会话 ID: {}, 结果: {}", userId, sessionId, result);
                
                // 2. 将结果流式返回
                // 模拟流式返回，将结果按空格分割成多个token
                String[] tokens = result.split(" ");
                for (String token : tokens) {
                    sink.next(token + " ");
                    fullResponse.append(token).append(" ");
                    // 模拟延迟，让流式效果更明显
                    Thread.sleep(50);
                }
                
                // 3. 存储对话历史
                ChatMessage userMessageEntity = new ChatMessage();
                userMessageEntity.setUserId(userId);
                userMessageEntity.setSessionId(sessionId);
                userMessageEntity.setRole("user");
                userMessageEntity.setContent(message);
                userMessageEntity.setCreatedAt(LocalDateTime.now());
                chatMessages.add(userMessageEntity);
                
                ChatMessage aiMessage = new ChatMessage();
                aiMessage.setUserId(userId);
                aiMessage.setSessionId(sessionId);
                aiMessage.setRole("assistant");
                aiMessage.setContent(fullResponse.toString().trim());
                aiMessage.setCreatedAt(LocalDateTime.now());
                chatMessages.add(aiMessage);
                
                sink.complete();
                log.info("流式返回完成，用户 ID: {}, 会话 ID: {}", userId, sessionId);
                
            } catch (Exception e) {
                log.error("处理用户消息（流式）失败", e);
                sink.error(e);
            }
        });
    }

    /**
     * 获取用户的对话历史
     * @param userId 用户 ID
     * @param sessionId 会话 ID
     * @param limit 限制数量
     * @return 对话历史列表
     */
    @Override
    public List<ChatMessage> getChatHistory(Long userId, String sessionId, int limit) {
        log.info("获取对话历史，用户 ID: {}, 会话 ID: {}, 限制：{}", userId, sessionId, limit);
        List<ChatMessage> result = new ArrayList<>();
        int count = 0;
        
        // 从后往前遍历，获取最近的消息
        for (int i = chatMessages.size() - 1; i >= 0 && count < limit; i--) {
            ChatMessage message = chatMessages.get(i);
            if (message.getUserId().equals(userId) && message.getSessionId().equals(sessionId)) {
                result.add(0, message); // 添加到列表开头，保持时间顺序
                count++;
            }
        }
        
        return result;
    }

    /**
     * 清除用户的对话历史
     * @param userId 用户 ID
     * @param sessionId 会话 ID
     */
    @Override
    public void clearChatHistory(Long userId, String sessionId) {
        log.info("清除对话历史，用户 ID: {}, 会话 ID: {}", userId, sessionId);
        chatMessages.removeIf(message -> message.getUserId().equals(userId) && message.getSessionId().equals(sessionId));
    }

    /**
     * 清除用户的所有对话历史
     * @param userId 用户 ID
     */
    @Override
    public void clearAllChatHistory(Long userId) {
        log.info("清除用户的所有对话历史，用户 ID: {}", userId);
        chatMessages.removeIf(message -> message.getUserId().equals(userId));
    }
}
