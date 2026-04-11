package com.travel.agent.service.ai;

import com.travel.agent.service.ai.impl.AIChatServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 快速模式功能测试
 */
@SpringBootTest
public class FastModeTest {

    @Autowired
    private AIChatService aiChatService;

    @Test
    public void testFastMode() {
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "推荐一个适合周末的旅行目的地";

        // 测试快速模式
        Flux<String> fastModeResponse = aiChatService.processMessageStream(userId, sessionId, message, true);

        StepVerifier.create(fastModeResponse)
                .expectNextCount(1) // 至少应该有一个响应
                .verifyComplete();
    }

    @Test
    public void testStandardMode() {
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "推荐一个适合周末的旅行目的地";

        // 测试标准模式
        Flux<String> standardModeResponse = aiChatService.processMessageStream(userId, sessionId, message, false);

        StepVerifier.create(standardModeResponse)
                .expectNextCount(1) // 至少应该有一个响应
                .verifyComplete();
    }

    @Test
    public void testBackwardCompatibility() {
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "推荐一个适合周末的旅行目的地";

        // 测试向后兼容性（无 fastMode 参数）
        Flux<String> response = aiChatService.processMessageStream(userId, sessionId, message);

        StepVerifier.create(response)
                .expectNextCount(1) // 至少应该有一个响应
                .verifyComplete();
    }
}