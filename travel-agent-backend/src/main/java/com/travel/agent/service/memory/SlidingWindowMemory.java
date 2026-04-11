package com.travel.agent.service.memory;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.entity.UserPreference;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 滑动窗口记忆组件
 * 管理对话窗口，超出限制时异步触发摘要生成
 */
@Component
@Slf4j
public class SlidingWindowMemory {
    private static final int WINDOW_SIZE = 10;
    private final Map<String, Deque<ChatMessage>> conversationWindows = new ConcurrentHashMap<>();
    private final Map<String, MessageWindowChatMemory> chatMemories = new ConcurrentHashMap<>();

    @Autowired
    private MemoryService memoryService;

    @Autowired
    private DatabaseChatMemoryStore chatMemoryStore;

    /**
     * 异步处理记忆任务
     * 生成摘要并提取用户偏好
     */
    @Async("memoryTaskExecutor")
    public void processMemoryAsync(String sessionKey, List<ChatMessage> messages) {
        if (memoryService == null) {
            log.warn("MemoryService未注入，跳过异步处理");
            return;
        }

        try {
            log.info("开始异步处理记忆任务，会话: {}", sessionKey);

            // 生成对话摘要
            String summary = memoryService.generateConversationSummary(messages);

            // 提取用户偏好
            List<UserPreference> preferences = memoryService.extractUserPreferences(messages);

            // 保存到数据库
            String[] sessionInfo = sessionKey.split("::");
            if (sessionInfo.length == 2) {
                Long userId = Long.parseLong(sessionInfo[0]);
                String sessionId = sessionInfo[1];

                // 保存摘要（覆盖模式）
                memoryService.saveConversationSummary(userId, sessionId, summary);

                // 保存用户偏好
                preferences.forEach(pref -> memoryService.saveUserPreference(userId, pref));
            }

            log.info("异步记忆处理完成，会话: {}", sessionKey);
        } catch (Exception e) {
            log.error("异步记忆处理失败，会话: {}", sessionKey, e);
        }
    }

    /**
     * 设置MemoryService（用于测试）
     */
    public void setMemoryService(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    /**
     * 获取当前窗口内容
     */
    public List<ChatMessage> getWindow(String sessionKey) {
        Deque<ChatMessage> window = conversationWindows.computeIfAbsent(
                sessionKey, k -> new LinkedList<>()
        );
        return new ArrayList<>(window);
    }

    /**
     * 获取或创建MessageWindowChatMemory实例
     */
    private MessageWindowChatMemory getOrCreateChatMemory(String sessionKey) {
        return chatMemories.computeIfAbsent(sessionKey, key ->
                MessageWindowChatMemory.builder()
                        .id(key)
                        .maxMessages(WINDOW_SIZE)
                        .build()
        );
    }

    /**
     * 获取LangChain4j MessageWindowChatMemory（用于智能体集成）
     */
    public MessageWindowChatMemory getChatMemory(String sessionKey) {
        return getOrCreateChatMemory(sessionKey);
    }

    /**
     * 添加消息到窗口
     * 超出窗口大小时触发异步摘要生成
     */
    public void addMessage(String sessionKey, ChatMessage message) {
        // 添加到自定义窗口（保持向后兼容）
        Deque<ChatMessage> window = conversationWindows.computeIfAbsent(
                sessionKey, k -> new LinkedList<>()
        );
        window.addLast(message);

        // 添加到官方MessageWindowChatMemory
        MessageWindowChatMemory chatMemory = getOrCreateChatMemory(sessionKey);
        dev.langchain4j.data.message.ChatMessage langChain4jMessage = MessageConverter.toLangChain4jMessage(message);
        chatMemory.add(langChain4jMessage);

        // 检查是否需要触发异步摘要生成
        if (window.size() > WINDOW_SIZE) {
            log.info("对话超出窗口限制，触发异步摘要生成，会话: {}", sessionKey);

            // 复制当前窗口内容进行异步处理
            List<ChatMessage> messagesToProcess = new ArrayList<>(window);
            processMemoryAsync(sessionKey, messagesToProcess);

            // 清理窗口，保留最近的几条作为上下文
            while (window.size() > 3) {
                window.removeFirst();
            }
        }
    }
}