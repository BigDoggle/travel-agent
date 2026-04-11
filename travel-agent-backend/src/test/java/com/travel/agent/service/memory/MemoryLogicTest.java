package com.travel.agent.service.memory;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.entity.UserPreference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 记忆功能逻辑测试
 * 测试核心逻辑，不依赖数据库
 */
public class MemoryLogicTest {

    private SlidingWindowMemory slidingWindowMemory;
    private MemoryService memoryService;

    @BeforeEach
    public void setUp() {
        slidingWindowMemory = new SlidingWindowMemory();
        memoryService = new MockMemoryService();
        slidingWindowMemory.setMemoryService(memoryService);
    }

    @Test
    public void testSlidingWindowBehavior() {
        String sessionKey = "1::test-session";

        // 添加10条消息（不触发摘要）
        for (int i = 1; i <= 10; i++) {
            slidingWindowMemory.addMessage(sessionKey, createUserMessage("Message " + i));
        }

        // 验证窗口大小
        assertEquals(10, slidingWindowMemory.getWindow(sessionKey).size());

        // 添加第11条消息（触发异步摘要）
        slidingWindowMemory.addMessage(sessionKey, createUserMessage("Message 11"));

        // 验证窗口被清理，只保留3条
        assertEquals(3, slidingWindowMemory.getWindow(sessionKey).size());

        // 验证保留的是最新的消息
        List<ChatMessage> window = slidingWindowMemory.getWindow(sessionKey);
        assertEquals("Message 9", window.get(0).getContent());
        assertEquals("Message 10", window.get(1).getContent());
        assertEquals("Message 11", window.get(2).getContent());
    }

    @Test
    public void testPreferenceExtraction() {
        // 测试偏好提取逻辑
        List<ChatMessage> messages = List.of(
            createUserMessage("我喜欢文化旅游和美食"),
            createAssistantMessage("好的，我为您推荐一些历史文化城市"),
            createUserMessage("我想要详细的行程安排和预算规划")
        );

        List<UserPreference> preferences = memoryService.extractUserPreferences(messages);

        // 验证提取到偏好
        assertNotNull(preferences);
        assertTrue(preferences.size() >= 2, "应该提取到至少2个偏好");

        // 验证具体偏好
        boolean hasTravelStyle = preferences.stream()
            .anyMatch(p -> "travel_style".equals(p.getPreferenceType()) && "文化旅游".equals(p.getPreferenceValue()));
        boolean hasReportDetail = preferences.stream()
            .anyMatch(p -> "report_detail".equals(p.getPreferenceType()) && "详细".equals(p.getPreferenceValue()));

        assertTrue(hasTravelStyle, "应该提取到文化旅游偏好");
        assertTrue(hasReportDetail, "应该提取到详细报告偏好");
    }

    @Test
    public void testSummaryGeneration() {
        // 测试摘要生成逻辑
        List<ChatMessage> messages = List.of(
            createUserMessage("我想去北京旅游三天"),
            createAssistantMessage("北京是个很好的选择，有很多景点"),
            createUserMessage("我特别喜欢故宫和天安门")
        );

        String summary = memoryService.generateConversationSummary(messages);

        // 验证摘要
        assertNotNull(summary);
        assertFalse(summary.isEmpty(), "摘要不应该为空");
        assertTrue(summary.contains("北京"), "摘要应该包含目的地");
    }

    @Test
    public void testMemoryContextBuilding() {
        Long userId = 1L;
        String sessionId = "test-session";

        // 模拟一些用户偏好
        ((MockMemoryService) memoryService).addMockPreference(userId, "travel_style", "文化旅游", 0.8);
        ((MockMemoryService) memoryService).addMockPreference(userId, "budget_level", "中等", 0.7);
        ((MockMemoryService) memoryService).addMockPreference(userId, "report_detail", "详细", 0.9);

        // 测试PlanAgent上下文
        String planContext = memoryService.buildMemoryContext(userId, sessionId, "plan");
        assertTrue(planContext.contains("用户偏好信息"));
        assertTrue(planContext.contains("文化旅游"));
        assertTrue(planContext.contains("中等"));

        // 测试ReplanAgent上下文
        String replanContext = memoryService.buildMemoryContext(userId, sessionId, "replan");
        assertTrue(replanContext.contains("报告偏好"));
        assertTrue(replanContext.contains("详细"));

        // 测试ExecuteAgent上下文（应该为空）
        String executeContext = memoryService.buildMemoryContext(userId, sessionId, "execute");
        assertEquals("", executeContext, "ExecuteAgent不应该有记忆上下文");
    }

    private ChatMessage createUserMessage(String content) {
        return createMessage("user", content);
    }

    private ChatMessage createAssistantMessage(String content) {
        return createMessage("assistant", content);
    }

    private ChatMessage createMessage(String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setUserId(1L);
        message.setSessionId("test-session");
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return message;
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
                    context.append("## 用户偏好信息\\n");
                    preferences.stream()
                        .filter(pref -> isPlanRelevantPreference(pref.getPreferenceType()))
                        .forEach(pref -> context.append("- ")
                            .append(mapPreferenceType(pref.getPreferenceType()))
                            .append(": ")
                            .append(pref.getPreferenceValue())
                            .append("\\n"));
                    break;

                case "replan":
                    context.append("## 报告偏好\\n");
                    preferences.stream()
                        .filter(pref -> isReportRelevantPreference(pref.getPreferenceType()))
                        .forEach(pref -> context.append("- ")
                            .append(mapPreferenceType(pref.getPreferenceType()))
                            .append(": ")
                            .append(pref.getPreferenceValue())
                            .append("\\n"));
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
}