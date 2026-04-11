package com.travel.agent.service.memory;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.entity.UserPreference;
import com.travel.agent.service.ai.AIChatService;
import com.travel.agent.service.memory.impl.MemoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 记忆功能完整流程测试
 */
@SpringBootTest
public class MemoryFlowTest {

    @Autowired
    private AIChatService aiChatService;

    @Autowired
    private MemoryService memoryService;

    @Autowired
    private SlidingWindowMemory slidingWindowMemory;

    @Test
    public void testFastModeMemoryIntegration() throws InterruptedException {
        Long userId = 1L;
        String sessionId = "test-fast-session";
        String sessionKey = userId + "::" + sessionId;

        // 1. 测试快速模式记忆功能
        String message1 = "我喜欢文化旅游，预算中等";
        Flux<String> response1 = aiChatService.processMessageStream(userId, sessionId, message1, true);

        StepVerifier.create(response1)
                .expectNextCount(1)
                .verifyComplete();

        // 等待异步处理完成
        TimeUnit.SECONDS.sleep(2);

        // 2. 验证滑动窗口记忆
        List<ChatMessage> windowMessages = slidingWindowMemory.getWindow(sessionKey);
        assertNotNull(windowMessages);
        assertTrue(windowMessages.size() >= 2, "滑动窗口应包含用户和AI消息");

        // 3. 验证记忆上下文构建
        String memoryContext = memoryService.buildMemoryContext(userId, sessionId, "fast");
        assertNotNull(memoryContext);
        System.out.println("记忆上下文: " + memoryContext);

        // 4. 验证用户偏好提取
        List<UserPreference> preferences = memoryService.getUserPreferences(userId);
        System.out.println("用户偏好数量: " + preferences.size());
        preferences.forEach(pref -> System.out.println(
                "偏好: " + pref.getPreferenceType() + " = " + pref.getPreferenceValue()));

        // 5. 第二次对话，验证记忆是否被使用
        String message2 = "推荐一些适合我的地方";
        Flux<String> response2 = aiChatService.processMessageStream(userId, sessionId, message2, true);

        StepVerifier.create(response2)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void testStandardModeMemoryIntegration() throws InterruptedException {
        Long userId = 2L;
        String sessionId = "test-standard-session";
        String sessionKey = userId + "::" + sessionId;

        // 1. 测试标准模式记忆功能
        String message1 = "帮我规划一个北京三日游，我喜欢历史古迹";
        Flux<String> response1 = aiChatService.processMessageStream(userId, sessionId, message1, false);

        StepVerifier.create(response1)
                .expectNextCount(1)
                .verifyComplete();

        // 等待异步处理完成
        TimeUnit.SECONDS.sleep(3);

        // 2. 验证滑动窗口记忆
        List<ChatMessage> windowMessages = slidingWindowMemory.getWindow(sessionKey);
        assertNotNull(windowMessages);
        assertTrue(windowMessages.size() >= 2, "滑动窗口应包含用户和AI消息");

        // 3. 验证记忆上下文构建
        String planContext = memoryService.buildMemoryContext(userId, sessionId, "plan");
        String replanContext = memoryService.buildMemoryContext(userId, sessionId, "replan");

        System.out.println("Plan上下文: " + planContext);
        System.out.println("Replan上下文: " + replanContext);

        // 4. 验证用户偏好
        List<UserPreference> preferences = memoryService.getUserPreferences(userId);
        System.out.println("标准模式用户偏好数量: " + preferences.size());
    }

    @Test
    public void testSlidingWindowFunctionality() throws InterruptedException {
        Long userId = 3L;
        String sessionId = "test-window-session";
        String sessionKey = userId + "::" + sessionId;

        // 添加多条消息触发滑动窗口机制
        for (int i = 1; i <= 12; i++) {
            String message = "测试消息 " + i;
            Flux<String> response = aiChatService.processMessageStream(userId, sessionId, message, true);

            StepVerifier.create(response)
                    .expectNextCount(1)
                    .verifyComplete();

            // 短暂等待确保消息处理
            TimeUnit.MILLISECONDS.sleep(100);
        }

        // 验证滑动窗口大小限制
        List<ChatMessage> windowMessages = slidingWindowMemory.getWindow(sessionKey);
        assertTrue(windowMessages.size() <= 5, "滑动窗口大小应被限制"); // 3条保留 + 可能的最新消息

        System.out.println("滑动窗口最终大小: " + windowMessages.size());
        windowMessages.forEach(msg -> System.out.println(
                "窗口消息: " + msg.getContent()));
    }

    @Test
    public void testMemorySummaryGeneration() throws InterruptedException {
        Long userId = 4L;
        String sessionId = "test-summary-session";

        // 进行多轮对话以生成摘要
        String[] messages = {
            "我想去日本旅游",
            "预算大概1万元左右",
            "喜欢文化历史和美食",
            "计划在春季出行"
        };

        for (String message : messages) {
            Flux<String> response = aiChatService.processMessageStream(userId, sessionId, message, true);

            StepVerifier.create(response)
                    .expectNextCount(1)
                    .verifyComplete();

            TimeUnit.MILLISECONDS.sleep(500);
        }

        // 等待异步摘要生成
        TimeUnit.SECONDS.sleep(3);

        // 验证摘要生成
        String summary = memoryService.getLatestConversationSummary(userId, sessionId);
        System.out.println("生成的摘要: " + summary);

        assertNotNull(summary);
        assertFalse(summary.isEmpty(), "摘要不应为空");
    }
}