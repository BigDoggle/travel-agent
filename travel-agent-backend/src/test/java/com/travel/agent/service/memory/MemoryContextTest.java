package com.travel.agent.service.memory;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.entity.MemorySummary;
import com.travel.agent.entity.UserPreference;
import com.travel.agent.mapper.ChatMessageMapper;
import com.travel.agent.mapper.MemorySummaryMapper;
import com.travel.agent.mapper.UserPreferenceMapper;
import com.travel.agent.service.memory.impl.MemoryServiceImpl;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 记忆上下文完整性测试
 * 验证是否正确查询了所有三个表：user_preference、memory_summary、chat_message
 */
public class MemoryContextTest {

    private MemoryService memoryService;

    @Mock
    private OpenAiChatModel chatModel;

    @Mock
    private UserPreferenceMapper userPreferenceMapper;

    @Mock
    private MemorySummaryMapper memorySummaryMapper;

    @Mock
    private ChatMessageMapper chatMessageMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        memoryService = new MemoryServiceImpl(chatModel, userPreferenceMapper, memorySummaryMapper, chatMessageMapper);
    }

    @Test
    public void testMemoryContextIncludesAllTables() {
        Long userId = 100L;
        String sessionId = "test-context-session";

        // 1. 准备模拟数据
        // 用户偏好数据
        UserPreference pref1 = createUserPreference(userId, "travel_style", "文化旅游", 0.8);
        UserPreference pref2 = createUserPreference(userId, "report_detail", "详细", 0.7);
        List<UserPreference> preferences = Arrays.asList(pref1, pref2);

        // 对话摘要数据
        MemorySummary summary = new MemorySummary();
        summary.setUserId(userId);
        summary.setSessionId(sessionId);
        summary.setSummaryType("conversation");
        summary.setContent("用户喜欢文化旅游，预算中等，关注美食体验");

        // 对话消息数据
        ChatMessage msg1 = createChatMessage(userId, sessionId, "user", "我想去一个有历史文化的地方旅游");
        ChatMessage msg2 = createChatMessage(userId, sessionId, "assistant", "推荐您去北京，有丰富的历史文化遗产");
        List<ChatMessage> recentMessages = Arrays.asList(msg1, msg2);

        // 2. 配置Mock行为
        when(userPreferenceMapper.findByUserId(userId)).thenReturn(preferences);
        when(memorySummaryMapper.findByUserSessionAndType(userId, sessionId, "conversation"))
            .thenReturn(summary);
        when(chatMessageMapper.findRecentByUserSession(userId, sessionId, 5))
            .thenReturn(recentMessages);

        // 3. 测试 FastAgent 记忆上下文（应该包含所有三个表的数据）
        String fastContext = memoryService.buildMemoryContext(userId, sessionId, "fast");
        System.out.println("FastAgent 记忆上下文:\n" + fastContext);

        // 验证包含用户偏好
        assertTrue(fastContext.contains("用户偏好信息"), "应包含用户偏好部分");
        assertTrue(fastContext.contains("旅行风格") || fastContext.contains("文化旅游"), "应包含旅行偏好");
        assertTrue(fastContext.contains("报告详细程度") || fastContext.contains("详细"), "应包含报告偏好");

        // 验证包含对话摘要
        assertTrue(fastContext.contains("对话摘要"), "应包含对话摘要部分");
        assertTrue(fastContext.contains("文化旅游") || fastContext.contains("美食体验"), "应包含摘要内容");

        // 验证包含最近对话
        assertTrue(fastContext.contains("最近对话"), "应包含最近对话部分");
        assertTrue(fastContext.contains("user:") || fastContext.contains("assistant:"), "应包含对话消息");

        // 4. 测试 PlanAgent 记忆上下文（应该包含用户偏好和对话摘要）
        String planContext = memoryService.buildMemoryContext(userId, sessionId, "plan");
        System.out.println("\nPlanAgent 记忆上下文:\n" + planContext);

        assertTrue(planContext.contains("对话摘要"), "PlanAgent 应包含对话摘要");
        assertTrue(planContext.contains("用户偏好信息"), "PlanAgent 应包含用户偏好");
        assertFalse(planContext.contains("最近对话"), "PlanAgent 不应包含最近对话");

        // 5. 测试 ReplanAgent 记忆上下文（应该包含报告偏好和对话摘要）
        String replanContext = memoryService.buildMemoryContext(userId, sessionId, "replan");
        System.out.println("\nReplanAgent 记忆上下文:\n" + replanContext);

        assertTrue(replanContext.contains("对话摘要"), "ReplanAgent 应包含对话摘要");
        assertTrue(replanContext.contains("报告偏好"), "ReplanAgent 应包含报告偏好");
        assertFalse(replanContext.contains("最近对话"), "ReplanAgent 不应包含最近对话");

        // 验证Mock方法被调用（chatMessageMapper只在fast模式下被调用，但其他模式也会调用）
        verify(userPreferenceMapper, times(3)).findByUserId(userId);
        verify(memorySummaryMapper, times(3)).findByUserSessionAndType(userId, sessionId, "conversation");
        verify(chatMessageMapper, atLeast(1)).findRecentByUserSession(userId, sessionId, 5);
    }

    @Test
    public void testMemoryContextWithEmptyData() {
        Long userId = 200L;
        String sessionId = "empty-test-session";

        // 配置空数据
        when(userPreferenceMapper.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(memorySummaryMapper.findByUserSessionAndType(userId, sessionId, "conversation"))
            .thenReturn(null);
        when(chatMessageMapper.findRecentByUserSession(userId, sessionId, 5))
            .thenReturn(Collections.emptyList());

        // 测试空数据情况
        String context = memoryService.buildMemoryContext(userId, sessionId, "fast");
        System.out.println("空数据测试结果: " + context);

        // 空数据时应该返回空字符串或包含默认标题
        assertTrue(context.isEmpty() || context.contains("## 用户偏好信息"), "空数据时应返回合理结果");
    }

    @Test
    public void testMemoryContextWithOnlyPreferences() {
        Long userId = 300L;
        String sessionId = "only-prefs-session";

        // 只有用户偏好，没有摘要和消息
        UserPreference pref = createUserPreference(userId, "travel_style", "探险旅游", 0.9);
        List<UserPreference> preferences = Collections.singletonList(pref);

        when(userPreferenceMapper.findByUserId(userId)).thenReturn(preferences);
        when(memorySummaryMapper.findByUserSessionAndType(userId, sessionId, "conversation"))
            .thenReturn(null);
        when(chatMessageMapper.findRecentByUserSession(userId, sessionId, 5))
            .thenReturn(Collections.emptyList());

        String context = memoryService.buildMemoryContext(userId, sessionId, "fast");
        System.out.println("只有偏好的测试结果: " + context);

        // 应该只包含用户偏好部分
        assertTrue(context.contains("用户偏好信息"), "应包含用户偏好");
        assertTrue(context.contains("探险旅游"), "应包含偏好值");
        assertFalse(context.contains("对话摘要"), "不应包含对话摘要");
        assertFalse(context.contains("最近对话"), "不应包含最近对话");
    }

    private UserPreference createUserPreference(Long userId, String type, String value, double confidence) {
        UserPreference pref = new UserPreference();
        pref.setUserId(userId);
        pref.setPreferenceType(type);
        pref.setPreferenceValue(value);
        pref.setConfidence(confidence);
        pref.setSource("测试数据");
        pref.setCreatedAt(LocalDateTime.now());
        pref.setUpdatedAt(LocalDateTime.now());
        return pref;
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
}