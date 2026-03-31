package com.travel.agent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户偏好实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreference {

    private Long id;

    private Long userId;

    private String preferenceType;

    private String preferenceValue;

    private String description;

    private Double confidence = 0.00;

    private String source;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
