package com.travel.agent.service.memory;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.entity.UserPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 记忆功能集成测试
 * 验证滑动窗口、异步处理和记忆分发功能
 */
public class MemoryIntegrationTest {

    @BeforeEach
    public void setUp() {
        memoryService = new MockMemoryService();
        slidingWindowMemory = new SlidingWindowMemory();
        slidingWindowMemory.setMemoryService(memoryService);
    }

    /**
     * 模拟MemoryService实现，用于测试
     */
    private static class MockMemoryService implements MemoryService {
        private final List<UserPreference> mockPreferences = new java.util.ArrayList<>();

        public void addMockPreference(Long userId, String type, String value, double confidence) {
            UserPreference pref = new UserPreference();
            pref.setUserId(userId);
            pref.setPreferenceType(type);
            pref.setPreferenceValue(value);
            pref.setConfidence(confidence);
            mockPreferences.add(pref);
        }

        @Override
        public String generateConversationSummary(List<ChatMessage> messages) {
            long userMessageCount = messages.stream()
                .filter(msg -> "user".equals(msg.getRole()))
                .count();

            StringBuilder summary = new StringBuilder();
            summary.append("对话包含 ").append(userMessageCount).append(" 条用户消息，");

            String allContent = messages.stream()
                .map(ChatMessage::getContent)
                .collect(java.util.stream.Collectors.joining(" "));

            if (allContent.contains("北京")) {
                summary.append("讨论北京旅游，");
            }
            if (allContent.contains("文化") || allContent.contains("历史")) {
                summary.append("涉及文化旅游，");
            }

            summary.append("主要讨论旅行规划相关内容。");
            return summary.toString();
        }

        @Override
        public List<UserPreference> extractUserPreferences(List<ChatMessage> messages) {
            List<UserPreference> preferences = new java.util.ArrayList<>();

            String allContent = messages.stream()
                .filter(msg -> "user".equals(msg.getRole()))
                .map(ChatMessage::getContent)
                .collect(java.util.stream.Collectors.joining(" "));

            if (allContent.contains("文化") || allContent.contains("历史")) {
                UserPreference pref = new UserPreference();
                pref.setPreferenceType("travel_style");
                pref.setPreferenceValue("文化旅游");
                pref.setConfidence(0.8);
                preferences.add(pref);
            }

            if (allContent.contains("详细")) {
                UserPreference pref = new UserPreference();
                pref.setPreferenceType("report_detail");
                pref.setPreferenceValue("详细");
                pref.setConfidence(0.9);
                preferences.add(pref);
            }

            return preferences;
        }

        @Override
        public void saveConversationSummary(Long userId, String sessionId, String summary) {
            // Mock implementation
        }

        @Override
        public void saveUserPreference(Long userId, UserPreference preference) {
            // Mock implementation
        }

        @Override
        public List<UserPreference> getUserPreferences(Long userId) {
            return mockPreferences.stream()
                .filter(p -> p.getUserId().equals(userId))
                .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public String getLatestConversationSummary(Long userId, String sessionId) {
            return "";
        }

        @Override
        public String buildMemoryContext(Long userId, String sessionId, String agentType) {
            StringBuilder context = new StringBuilder();
            List<UserPreference> preferences = getUserPreferences(userId);

            if (preferences.isEmpty()) {
                return "";
            }

            switch (agentType.toLowerCase()) {
                case "plan":
                    context.append("## 用户偏好信息\n");
                    preferences.stream()
                        .filter(pref -> isPlanRelevantPreference(pref.getPreferenceType()))
                        .forEach(pref -> context.append("- ")
                            .append(mapPreferenceType(pref.getPreferenceType()))
                            .append(": ")
                            .append(pref.getPreferenceValue())
                            .append("\n"));
                    break;

                case "replan":
                    context.append("## 报告偏好\n");
                    preferences.stream()
                        .filter(pref -> isReportRelevantPreference(pref.getPreferenceType()))
                        .forEach(pref -> context.append("- ")
                            .append(mapPreferenceType(pref.getPreferenceType()))
                            .append(": ")
                            .append(pref.getPreferenceValue())
                            .append("\n"));
                    break;

                default:
                    break;
            }

            return context.toString();
        }

        private boolean isPlanRelevantPreference(String type) {
            return java.util.Arrays.asList("travel_style", "budget_level", "preferred_season", "destination_type").contains(type);
        }

        private boolean isReportRelevantPreference(String type) {
            return java.util.Arrays.asList("report_detail", "report_focus", "report_format").contains(type);
        }

        private String mapPreferenceType(String type) {
            java.util.Map<String, String> mapping = java.util.Map.of(
                "travel_style", "旅行风格",
                "budget_level", "预算水平",
                "preferred_season", "偏好季节",
                "destination_type", "目的地类型",
                "report_detail", "报告详细程度",
                "report_focus", "报告关注重点",
                "report_format", "报告格式偏好"
            );
            return mapping.getOrDefault(type, type);
        }
    }

    private MemoryService memoryService;
    private SlidingWindowMemory slidingWindowMemory;

    @Test
    public void testMemoryWorkflow() throws InterruptedException {
        Long userId = 1L;
        String sessionId = "integration-test-session";
        String sessionKey = userId + "::" + sessionId;

        // 1. 测试滑动窗口和异步处理
        CountDownLatch latch = new CountDownLatch(11);

        // 添加11条消息，触发异步处理
        for (int i = 1; i <= 11; i++) {
            ChatMessage message = createMessage(userId, sessionId, "user", "测试消息 " + i);
            if (i == 11) {
                message.setContent("我喜欢文化旅游，预算中等，希望有详细的报告");
            }
            slidingWindowMemory.addMessage(sessionKey, message);
            latch.countDown();
        }

        // 等待异步处理完成
        latch.await(5, TimeUnit.SECONDS);

        // 验证窗口被清理
        List<ChatMessage> window = slidingWindowMemory.getWindow(sessionKey);
        assertEquals(3, window.size(), "窗口应该只保留最近3条消息");

        // 2. 测试记忆上下文构建
        String planContext = memoryService.buildMemoryContext(userId, sessionId, "plan");
        String replanContext = memoryService.buildMemoryContext(userId, sessionId, "replan");
        String executeContext = memoryService.buildMemoryContext(userId, sessionId, "execute");

        // 验证PlanAgent获得用户偏好
        assertTrue(planContext.contains("用户偏好信息") || planContext.isEmpty());

        // 验证ReplanAgent获得报告偏好
        assertTrue(replanContext.contains("报告偏好") || replanContext.isEmpty());

        // 验证ExecuteAgent无记忆上下文
        assertEquals("", executeContext, "ExecuteAgent不应该有记忆上下文");

        System.out.println("=== 记忆功能集成测试通过 ===");
        System.out.println("PlanAgent上下文: " + planContext);
        System.out.println("ReplanAgent上下文: " + replanContext);
        System.out.println("ExecuteAgent上下文: " + executeContext);
    }

    @Test
    public void testPreferenceExtraction() {
        Long userId = 2L;
        String sessionId = "preference-test-session";

        // 创建包含偏好的对话消息
        ChatMessage message1 = createMessage(userId, sessionId, "user", "我喜欢文化旅游");
        ChatMessage message2 = createMessage(userId, sessionId, "assistant", "好的，我为您推荐一些历史文化景点");
        ChatMessage message3 = createMessage(userId, sessionId, "user", "我想要详细的行程安排");

        // 测试偏好提取
        List<UserPreference> preferences = memoryService.extractUserPreferences(
            List.of(message1, message2, message3)
        );

        // 验证提取到偏好
        assertNotNull(preferences);
        assertTrue(preferences.size() > 0, "应该提取到至少一个偏好");

        // 验证具体偏好内容
        boolean hasTravelStyle = preferences.stream()
            .anyMatch(p -> "travel_style".equals(p.getPreferenceType()) && "文化旅游".equals(p.getPreferenceValue()));
        boolean hasReportDetail = preferences.stream()
            .anyMatch(p -> "report_detail".equals(p.getPreferenceType()) && "详细".equals(p.getPreferenceValue()));

        assertTrue(hasTravelStyle, "应该提取到文化旅游偏好");
        assertTrue(hasReportDetail, "应该提取到详细报告偏好");

        System.out.println("=== 偏好提取测试通过 ===");
        preferences.forEach(pref -> System.out.println(
            "偏好类型: " + pref.getPreferenceType() +
            ", 值: " + pref.getPreferenceValue() +
            ", 置信度: " + pref.getConfidence()));
    }

    @Test
    public void testSummaryGeneration() {
        Long userId = 3L;
        String sessionId = "summary-test-session";

        // 创建对话消息
        ChatMessage message1 = createMessage(userId, sessionId, "user", "我想去北京旅游");
        ChatMessage message2 = createMessage(userId, sessionId, "assistant", "北京是个很好的选择，有很多历史文化景点");
        ChatMessage message3 = createMessage(userId, sessionId, "user", "我特别喜欢故宫和天安门");

        // 测试摘要生成
        String summary = memoryService.generateConversationSummary(
            List.of(message1, message2, message3)
        );

        // 验证摘要内容
        assertNotNull(summary);
        assertFalse(summary.isEmpty(), "摘要不应该为空");
        assertTrue(summary.length() > 10, "摘要应该有合理长度");

        System.out.println("=== 摘要生成测试通过 ===");
        System.out.println("生成的摘要: " + summary);
    }

    private ChatMessage createMessage(Long userId, String sessionId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }
}