package com.travel.agent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 记忆摘要实体
 * 用于存储对话摘要，采用覆盖更新模式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemorySummary {

    private Long id;

    private Long userId;

    private String sessionId;

    private String summaryType;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}