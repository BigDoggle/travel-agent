package com.travel.agent.controller;

import com.travel.agent.common.Result;
import com.travel.agent.entity.ChatMessage;
import com.travel.agent.service.ai.AIChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * AI对话控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/chat")
public class AIChatController extends BaseController {

    @Autowired
    private AIChatService aiChatService;

    /**
     * 处理用户输入，流式返回AI响应
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param message 用户输入消息
     * @return SseEmitter 用于流式返回
     */
    @PostMapping("/message")
    public SseEmitter processMessageStream(@RequestParam Long userId, @RequestParam String sessionId, @RequestBody String message) {
        log.info("处理用户消息（流式），用户ID: {}, 会话ID: {}, 消息: {}", userId, sessionId, message);
        SseEmitter emitter = new SseEmitter();
        
        try {
            // 调用服务层的流式处理方法
            aiChatService.processMessageStream(userId, sessionId, message, token -> {
                try {
                    emitter.send(SseEmitter.event()
                            .data(token)
                            .name("message")
                            .reconnectTime(5000L));
                } catch (IOException e) {
                    log.error("发送流式消息失败", e);
                    emitter.completeWithError(e);
                }
            });
            
            // 完成流式传输
            emitter.onCompletion(() -> {
                log.info("流式传输完成，用户ID: {}, 会话ID: {}", userId, sessionId);
            });
            
        } catch (Exception e) {
            log.error("处理用户消息（流式）失败", e);
            emitter.completeWithError(e);
        }
        
        return emitter;
    }

    /**
     * 获取用户的对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 对话历史列表
     */
    @GetMapping("/history")
    public Result<List<ChatMessage>> getChatHistory(@RequestParam Long userId, @RequestParam String sessionId, @RequestParam(defaultValue = "50") int limit) {
        log.info("获取对话历史，用户ID: {}, 会话ID: {}, 限制: {}", userId, sessionId, limit);
        try {
            List<ChatMessage> history = aiChatService.getChatHistory(userId, sessionId, limit);
            return Result.success("获取成功", history);
        } catch (Exception e) {
            log.error("获取对话历史失败", e);
            return Result.error("获取失败: " + e.getMessage());
        }
    }

    /**
     * 清除用户的对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 操作结果
     */
    @DeleteMapping("/history")
    public Result<Void> clearChatHistory(@RequestParam Long userId, @RequestParam String sessionId) {
        log.info("清除对话历史，用户ID: {}, 会话ID: {}", userId, sessionId);
        try {
            aiChatService.clearChatHistory(userId, sessionId);
            return Result.success("清除成功", null);
        } catch (Exception e) {
            log.error("清除对话历史失败", e);
            return Result.error("清除失败: " + e.getMessage());
        }
    }

    /**
     * 清除用户的所有对话历史
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/history/all")
    public Result<Void> clearAllChatHistory(@RequestParam Long userId) {
        log.info("清除用户的所有对话历史，用户ID: {}", userId);
        try {
            aiChatService.clearAllChatHistory(userId);
            return Result.success("清除成功", null);
        } catch (Exception e) {
            log.error("清除用户的所有对话历史失败", e);
            return Result.error("清除失败: " + e.getMessage());
        }
    }
}