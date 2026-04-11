package com.travel.agent.service.ai.impl;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.mapper.ChatMessageMapper;
import com.travel.agent.service.ai.AIChatService;
import com.travel.agent.service.ai.FastAgent;
import com.travel.agent.service.ai.MCPService;
import com.travel.agent.service.memory.MemoryService;
import com.travel.agent.service.memory.SlidingWindowMemory;
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

    @Autowired
    private FastAgent fastAgent;

    @Autowired
    private MemoryService memoryService;

    @Autowired
    private SlidingWindowMemory slidingWindowMemory;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    // 简单的内存存储，用于模拟对话历史
    private final List<ChatMessage> chatMessages = new ArrayList<>();

    /**
     * 处理用户输入，流式返回 AI 响应
     *
     * @param userId    用户 ID
     * @param sessionId 会话 ID
     * @param message   用户输入消息
     * @return Flux<String> 流式返回的 AI 响应
     */
    @Override
    public Flux<String> processMessageStream(Long userId, String sessionId, String message) {
        // 默认使用标准模式以保持向后兼容
        return processMessageStream(userId, sessionId, message, false);
    }

    @Override
    public Flux<String> processMessageStream(Long userId, String sessionId, String message, boolean fastMode) {
        log.info("处理用户消息（流式），用户 ID: {}, 会话 ID: {}, 消息：{}", userId, sessionId, message);

        return Flux.create(sink -> {
            StringBuilder fullResponse = new StringBuilder();

            try {
                String result;
                String sessionKey = userId + "::" + sessionId;

                // 1. 准备记忆上下文（快速模式和标准模式都使用）
                String memoryContext = memoryService.buildMemoryContext(userId, sessionId, "fast");
                String enhancedInput = String.format("""
                        用户需求: %s
                        
                        %s
                        
                        请基于以上记忆上下文，提供个性化的旅行建议。
                        """, message, memoryContext);

                // 2. 保存用户输入到滑动窗口和数据库
                ChatMessage userMessage = createChatMessage(userId, sessionId, "user", message);
                slidingWindowMemory.addMessage(sessionKey, userMessage);
                chatMessageMapper.insert(userMessage);

                if (fastMode) {
                    // 使用快速模式 - 单智能体直接响应（带记忆上下文）
                    result = fastAgent.chat(enhancedInput);
                    log.info("快速模式完成，用户 ID: {}, 会话 ID: {}, 结果长度: {}", userId, sessionId, result.length());
                } else {
                    // 使用标准模式 - 多智能体协调（MCPService内部会处理记忆）
                    result = mcpService.coordinateAgents(userId, sessionId, message);
                    log.info("多智能体协调完成，用户 ID: {}, 会话 ID: {}, 结果长度: {}", userId, sessionId, result.length());
                }

                // 3. 保存AI响应到滑动窗口和数据库
                ChatMessage aiMessage = createChatMessage(userId, sessionId, "assistant", result);
                slidingWindowMemory.addMessage(sessionKey, aiMessage);
                chatMessageMapper.insert(aiMessage);

                // 2. 将结果流式返回
                // 模拟流式返回，将结果按空格分割成多个token
                String[] tokens = result.split(" ");
                for (String token : tokens) {
                    sink.next(token + " ");
                    fullResponse.append(token).append(" ");
                    // 模拟延迟，让流式效果更明显
                    Thread.sleep(50);
                }

                sink.complete();
                log.info("流式返回完成，用户 ID: {}, 会话 ID: {}", userId, sessionId);

            } catch (Exception e) {
                log.error("处理用户消息（流式）失败", e);
                sink.error(e);
            }
        });
    }

    private ChatMessage createChatMessage(Long userId, String sessionId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }

    /**
     * 获取用户的对话历史
     *
     * @param userId    用户 ID
     * @param sessionId 会话 ID
     * @param limit     限制数量
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
     *
     * @param userId    用户 ID
     * @param sessionId 会话 ID
     */
    @Override
    public void clearChatHistory(Long userId, String sessionId) {
        log.info("清除对话历史，用户 ID: {}, 会话 ID: {}", userId, sessionId);
        chatMessages.removeIf(message -> message.getUserId().equals(userId) && message.getSessionId().equals(sessionId));
    }

    /**
     * 清除用户的所有对话历史
     *
     * @param userId 用户 ID
     */
    @Override
    public void clearAllChatHistory(Long userId) {
        log.info("清除用户的所有对话历史，用户 ID: {}", userId);
        chatMessages.removeIf(message -> message.getUserId().equals(userId));
    }
}
