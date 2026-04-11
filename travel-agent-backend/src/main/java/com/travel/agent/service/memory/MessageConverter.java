package com.travel.agent.service.memory;

import com.travel.agent.entity.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息格式转换工具类
 * 负责在自定义ChatMessage和LangChain4j标准消息类型之间进行转换
 */
@Slf4j
public class MessageConverter {

    /**
     * 将自定义ChatMessage转换为LangChain4j ChatMessage
     */
    public static dev.langchain4j.data.message.ChatMessage toLangChain4jMessage(ChatMessage customMessage) {
        String role = customMessage.getRole();
        String content = customMessage.getContent();

        switch (role.toLowerCase()) {
            case "user":
                return UserMessage.from(content);
            case "assistant":
            case "ai":
                // 由于LangChain4j 1.12.1可能没有AiMessage，暂时转换为UserMessage
                return UserMessage.from("[AI] " + content);
            case "system":
                // 由于LangChain4j 1.12.1可能没有SystemMessage，暂时转换为UserMessage
                return UserMessage.from("[SYSTEM] " + content);
            default:
                log.warn("未知的消息角色: {}，默认为用户消息", role);
                return UserMessage.from(content);
        }
    }

    /**
     * 将LangChain4j ChatMessage转换为自定义ChatMessage
     */
    public static ChatMessage fromLangChain4jMessage(dev.langchain4j.data.message.ChatMessage langChain4jMessage,
                                                     Long userId, String sessionId) {
        ChatMessage customMessage = new ChatMessage();
        customMessage.setUserId(userId);
        customMessage.setSessionId(sessionId);
        // 注意：这里需要根据实际的LangChain4j API调整
        customMessage.setContent(langChain4jMessage.toString());
        customMessage.setCreatedAt(java.time.LocalDateTime.now());

        // 根据消息类型设置角色（简化版本）
        if (langChain4jMessage instanceof UserMessage) {
            String content = langChain4jMessage.toString();
            if (content.startsWith("[AI]")) {
                customMessage.setRole("assistant");
            } else if (content.startsWith("[SYSTEM]")) {
                customMessage.setRole("system");
            } else {
                customMessage.setRole("user");
            }
        } else {
            customMessage.setRole("unknown");
        }

        return customMessage;
    }

    /**
     * 批量转换自定义消息到LangChain4j消息
     */
    public static List<dev.langchain4j.data.message.ChatMessage> toLangChain4jMessages(List<ChatMessage> customMessages) {
        return customMessages.stream()
                .map(MessageConverter::toLangChain4jMessage)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换LangChain4j消息到自定义消息
     */
    public static List<ChatMessage> fromLangChain4jMessages(List<dev.langchain4j.data.message.ChatMessage> langChain4jMessages,
                                                           Long userId, String sessionId) {
        return langChain4jMessages.stream()
                .map(msg -> fromLangChain4jMessage(msg, userId, sessionId))
                .collect(Collectors.toList());
    }

    /**
     * 从LangChain4j消息类型获取角色字符串
     */
    public static String getRoleFromLangChain4jMessage(dev.langchain4j.data.message.ChatMessage message) {
        if (message instanceof UserMessage) {
            String content = message.toString();
            if (content.startsWith("[AI]")) {
                return "assistant";
            } else if (content.startsWith("[SYSTEM]")) {
                return "system";
            } else {
                return "user";
            }
        } else {
            return "unknown";
        }
    }
}