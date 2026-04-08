package com.travel.agent.controller;

import com.travel.agent.service.ai.AIChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * AI 聊天控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIChatController {

    @Autowired
    private AIChatService aiChatService;

    /**
     * 流式处理用户消息
     */
    @PostMapping("/chat/stream")
    public SseEmitter streamChat(
            @RequestParam Long userId,
            @RequestParam String sessionId,
            @RequestBody String message) {
        SseEmitter emitter = new SseEmitter();

        // 异步处理流式返回
        new Thread(() -> {
            try {
                // 调用服务层的流式处理方法
                aiChatService.processMessageStream(userId, sessionId, message)
                        .subscribe(
                                token -> {
                                    try {
                                        emitter.send(SseEmitter.event()
                                                .data(token)
                                                .name("message")
                                                .reconnectTime(5000L));
                                    } catch (IOException e) {
                                        log.error("发送流式消息失败", e);
                                        emitter.completeWithError(e);
                                    }
                                },
                                error -> {
                                    log.error("流式处理出错", error);
                                    emitter.completeWithError(error);
                                },
                                () -> {
                                    log.info("流式处理完成");
                                    emitter.complete();
                                }
                        );
            } catch (Exception e) {
                log.error("处理流式请求失败", e);
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/chat/history")
    public Object getChatHistory(
            @RequestParam Long userId,
            @RequestParam String sessionId,
            @RequestParam(defaultValue = "20") int limit) {
        return aiChatService.getChatHistory(userId, sessionId, limit);
    }

    /**
     * 清除对话历史
     */
    @DeleteMapping("/chat/history")
    public Object clearChatHistory(
            @RequestParam Long userId,
            @RequestParam String sessionId) {
        aiChatService.clearChatHistory(userId, sessionId);
        return "对话历史已清除";
    }

    /**
     * 清除所有对话历史
     */
    @DeleteMapping("/chat/history/all")
    public Object clearAllChatHistory(@RequestParam Long userId) {
        aiChatService.clearAllChatHistory(userId);
        return "所有对话历史已清除";
    }
}
