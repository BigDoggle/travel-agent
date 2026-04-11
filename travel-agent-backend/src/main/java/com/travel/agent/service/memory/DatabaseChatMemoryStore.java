package com.travel.agent.service.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库支持的记忆存储实现
 * 用于缓存和管理对话记忆
 */
@Component
@Slf4j
public class DatabaseChatMemoryStore {

    // 临时内存缓存，实际应用中应该使用数据库
    private final Map<Object, List<dev.langchain4j.data.message.ChatMessage>> memoryCache = new ConcurrentHashMap<>();

    @Autowired
    private com.travel.agent.mapper.ChatMessageMapper chatMessageMapper;

    public List<dev.langchain4j.data.message.ChatMessage> getMessages(Object memoryId) {
        try {
            // 从缓存获取
            List<dev.langchain4j.data.message.ChatMessage> cachedMessages = memoryCache.get(memoryId);
            if (cachedMessages != null) {
                return cachedMessages;
            }

            // 从数据库获取
            String sessionId = memoryId.toString();
            List<com.travel.agent.entity.ChatMessage> dbMessages = chatMessageMapper.findBySession(sessionId);
            List<dev.langchain4j.data.message.ChatMessage> messages = MessageConverter.toLangChain4jMessages(dbMessages);

            // 更新缓存
            memoryCache.put(memoryId, messages);

            return messages;
        } catch (Exception e) {
            log.error("获取记忆消息失败，memoryId: {}", memoryId, e);
            return List.of();
        }
    }

    public void updateMessages(Object memoryId, List<dev.langchain4j.data.message.ChatMessage> messages) {
        try {
            // 更新缓存
            memoryCache.put(memoryId, messages);

            // 保存到数据库
            String sessionId = memoryId.toString();
            // 注意：这里需要从sessionId中提取userId，实际应用中应该通过参数传递
            // List<com.travel.agent.entity.ChatMessage> dbMessages = MessageConverter.fromLangChain4jMessages(
            //     messages, userId, sessionId);
            // dbMessages.forEach(chatMessageMapper::insert);

            log.debug("更新记忆消息成功，memoryId: {}, 消息数: {}", memoryId, messages.size());
        } catch (Exception e) {
            log.error("更新记忆消息失败，memoryId: {}", memoryId, e);
        }
    }

    public void deleteMessages(Object memoryId) {
        try {
            // 删除缓存
            memoryCache.remove(memoryId);

            // 删除数据库记录（需要实现相应的mapper方法）
            String sessionId = memoryId.toString();
            // chatMessageMapper.deleteBySessionId(sessionId);

            log.debug("删除记忆消息成功，memoryId: {}", memoryId);
        } catch (Exception e) {
            log.error("删除记忆消息失败，memoryId: {}", memoryId, e);
        }
    }
}