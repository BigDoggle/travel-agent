package com.travel.agent.service.ai;

import com.travel.agent.service.ai.impl.AIChatServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单的快速模式功能测试
 */
@SpringBootTest
public class SimpleFastModeTest {

    @Autowired
    private AIChatService aiChatService;

    @Test
    public void testFastModeBasicFunctionality() {
        // 测试快速模式基本功能
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "你好";

        Flux<String> response = aiChatService.processMessageStream(userId, sessionId, message, true);

        assertNotNull(response, "快速模式响应不应为空");

        // 验证响应流可以正常创建和订阅
        StepVerifier.create(response)
                .expectNextCount(1) // 至少应该有一个响应
                .verifyComplete();
    }

    @Test
    public void testStandardModeBasicFunctionality() {
        // 测试标准模式基本功能
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "你好";

        Flux<String> response = aiChatService.processMessageStream(userId, sessionId, message, false);

        assertNotNull(response, "标准模式响应不应为空");

        // 验证响应流可以正常创建和订阅
        StepVerifier.create(response)
                .expectNextCount(1) // 至少应该有一个响应
                .verifyComplete();
    }

    @Test
    public void testBackwardCompatibilityFunctionality() {
        // 测试向后兼容性
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "你好";

        Flux<String> response = aiChatService.processMessageStream(userId, sessionId, message);

        assertNotNull(response, "向后兼容模式响应不应为空");

        // 验证响应流可以正常创建和订阅
        StepVerifier.create(response)
                .expectNextCount(1) // 至少应该有一个响应
                .verifyComplete();
    }

    @Test
    public void testMethodOverloading() {
        // 测试方法重载是否正确实现
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "测试消息";

        // 测试三个不同的方法调用
        Flux<String> response1 = aiChatService.processMessageStream(userId, sessionId, message);
        Flux<String> response2 = aiChatService.processMessageStream(userId, sessionId, message, true);
        Flux<String> response3 = aiChatService.processMessageStream(userId, sessionId, message, false);

        assertNotNull(response1, "方法1响应不应为空");
        assertNotNull(response2, "方法2响应不应为空");
        assertNotNull(response3, "方法3响应不应为空");
    }
}