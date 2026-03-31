package com.travel.agent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天消息实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private Long id;

    private String sessionId;

    private Long userId;

    private String role;

    private String content;

    private String metadata;

    private LocalDateTime createdAt;
}
