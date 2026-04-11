package com.travel.agent.entity;

import lombok.Data;

/**
 * 聊天请求数据传输对象
 */
@Data
public class ChatRequest {
    private Long userId;
    private String sessionId;
    private String message;
    private boolean fastMode;

    public ChatRequest() {
    }

    public ChatRequest(Long userId, String sessionId, String message, boolean fastMode) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.message = message;
        this.fastMode = fastMode;
    }

}