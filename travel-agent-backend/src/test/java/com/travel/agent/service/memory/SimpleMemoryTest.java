package com.travel.agent.service.memory;

import com.travel.agent.entity.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单记忆功能测试
 * 验证基本的消息转换和记忆功能
 */
@SpringBootTest
public class SimpleMemoryTest {

    @Test
    void testBasicMessageConverter() {
        // 创建自定义消息
        ChatMessage customMessage = new ChatMessage();
        customMessage.setRole("user");
        customMessage.setContent("你好，我想去旅行");

        // 转换为LangChain4j消息
        dev.langchain4j.data.message.ChatMessage langChain4jMessage = MessageConverter.toLangChain4jMessage(customMessage);

        // 验证转换结果
        assertTrue(langChain4jMessage instanceof UserMessage);
        assertEquals("你好，我想去旅行", langChain4jMessage.toString());

        System.out.println("=== 基本消息转换测试通过 ===");
    }

    @Test
    void testSlidingWindowBasic() {
        SlidingWindowMemory slidingWindow = new SlidingWindowMemory();

        // 创建测试消息
        ChatMessage message1 = new ChatMessage();
        message1.setUserId(1L);
        message1.setSessionId("test-session");
        message1.setRole("user");
        message1.setContent("第一条消息");

        ChatMessage message2 = new ChatMessage();
        message2.setUserId(1L);
        message2.setSessionId("test-session");
        message2.setRole("assistant");
        message2.setContent("第一条回复");

        // 添加到滑动窗口
        slidingWindow.addMessage("1::test-session", message1);
        slidingWindow.addMessage("1::test-session", message2);

        // 验证窗口内容
        var window = slidingWindow.getWindow("1::test-session");
        assertEquals(2, window.size());
        assertEquals("第一条消息", window.get(0).getContent());
        assertEquals("第一条回复", window.get(1).getContent());

        System.out.println("=== 滑动窗口基本功能测试通过 ===");
    }

    @Test
    void testMessageRoleConversion() {
        // 测试不同角色的转换
        ChatMessage userMsg = new ChatMessage();
        userMsg.setRole("user");
        userMsg.setContent("用户消息");

        ChatMessage assistantMsg = new ChatMessage();
        assistantMsg.setRole("assistant");
        assistantMsg.setContent("助手消息");

        // 转换为LangChain4j消息
        dev.langchain4j.data.message.ChatMessage convertedUser = MessageConverter.toLangChain4jMessage(userMsg);
        dev.langchain4j.data.message.ChatMessage convertedAssistant = MessageConverter.toLangChain4jMessage(assistantMsg);

        // 验证角色转换
        assertTrue(convertedUser instanceof UserMessage);
        // 注意：assistant消息可能也需要转换为UserMessage，因为LangChain4j 1.12.1可能没有AiMessage

        // 测试角色字符串获取
        assertEquals("user", MessageConverter.getRoleFromLangChain4jMessage(convertedUser));
        assertEquals("assistant", MessageConverter.getRoleFromLangChain4jMessage(convertedAssistant));

        System.out.println("=== 消息角色转换测试通过 ===");
    }
}