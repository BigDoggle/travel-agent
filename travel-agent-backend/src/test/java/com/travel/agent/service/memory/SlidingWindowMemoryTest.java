package com.travel.agent.service.memory;

import com.travel.agent.entity.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SlidingWindowMemoryTest {

    private SlidingWindowMemory slidingWindowMemory;


    @BeforeEach
    public void setUp() {
        slidingWindowMemory = new SlidingWindowMemory();
    }

    @Test
    public void testSlidingWindowTrigger() throws InterruptedException {
        String sessionKey = "1::test-session";

        // 添加10条消息（不触发摘要）
        for (int i = 1; i <= 10; i++) {
            slidingWindowMemory.addMessage(sessionKey, createMessage("Message " + i));
        }

        // 验证窗口大小
        assertEquals(10, slidingWindowMemory.getWindow(sessionKey).size());

        // 添加第11条消息（触发异步摘要）
        slidingWindowMemory.addMessage(sessionKey, createMessage("Message 11"));

        // 验证窗口被清理，只保留3条
        assertEquals(3, slidingWindowMemory.getWindow(sessionKey).size());

        // 等待异步任务完成
        Thread.sleep(2000);
    }

    @Test
    public void testWindowManagement() {
        String sessionKey = "2::window-test";

        // 添加5条消息
        for (int i = 1; i <= 5; i++) {
            slidingWindowMemory.addMessage(sessionKey, createMessage("Test Message " + i));
        }

        List<ChatMessage> window = slidingWindowMemory.getWindow(sessionKey);
        assertEquals(5, window.size());

        // 验证消息顺序
        assertEquals("Test Message 1", window.get(0).getContent());
        assertEquals("Test Message 5", window.get(4).getContent());
    }

    private ChatMessage createMessage(String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId("test-session");
        message.setUserId(1L);
        message.setRole("user");
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }
}