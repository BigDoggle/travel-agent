package com.travel.agent.service.ai;

import com.travel.agent.service.ai.impl.AIChatServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * AI聊天服务测试
 */
@Slf4j
@SpringBootTest
public class AIChatServiceTest {

    @Autowired
    private AIChatService aiChatService;

    /**
     * 测试流式返回功能
     */
    @Test
    public void testProcessMessageStream() {
        log.info("开始测试流式返回功能");
        
        // 模拟用户ID和会话ID
        Long userId = 1L;
        String sessionId = "test-session-" + System.currentTimeMillis();
        String message = "你好，帮我规划一次北京之旅";
        
        // 调用流式处理方法
        Flux<String> flux = aiChatService.processMessageStream(userId, sessionId, message);
        
        // 收集流式返回的数据
        List<String> receivedTokens = new ArrayList<>();
        
        // 验证Flux是否正常工作
        StepVerifier.create(flux)
                .recordWith(() -> new ArrayList<>())
                .thenConsumeWhile(token -> {
                    receivedTokens.add(token);
                    log.info("接收到token: {}", token);
                    return true;
                })
                .expectComplete()
                .verify(Duration.ofMinutes(1));
        
        // 验证是否接收到了数据
        assert !receivedTokens.isEmpty() : "没有接收到流式返回的数据";
        log.info("流式返回测试完成，共接收到 {} 个token", receivedTokens.size());
        
        // 验证返回的数据是否有意义
        String fullResponse = String.join("", receivedTokens);
        assert !fullResponse.isEmpty() : "返回的响应为空";
        log.info("完整响应: {}", fullResponse);
    }

    /**
     * 测试对话历史功能
     */
    @Test
    public void testGetChatHistory() {
        log.info("开始测试对话历史功能");
        
        // 模拟用户ID和会话ID
        Long userId = 1L;
        String sessionId = "test-session-" + System.currentTimeMillis();
        
        // 先发送一条消息
        String message = "你好，帮我规划一次北京之旅";
        Flux<String> flux = aiChatService.processMessageStream(userId, sessionId, message);
        
        // 等待流式返回完成
        StepVerifier.create(flux)
                .expectNextCount(1)
                .expectComplete()
                .verify(Duration.ofMinutes(1));
        
        // 获取对话历史
        var history = aiChatService.getChatHistory(userId, sessionId, 50);
        assert !history.isEmpty() : "对话历史为空";
        log.info("获取到 {} 条对话历史", history.size());
        
        // 验证对话历史中包含刚才发送的消息
        boolean hasUserMessage = history.stream()
                .anyMatch(msg -> "user".equals(msg.getRole()) && message.equals(msg.getContent()));
        assert hasUserMessage : "对话历史中不包含用户消息";
        
        // 验证对话历史中包含AI的回复
        boolean hasAssistantMessage = history.stream()
                .anyMatch(msg -> "assistant".equals(msg.getRole()));
        assert hasAssistantMessage : "对话历史中不包含AI回复";
    }
}
