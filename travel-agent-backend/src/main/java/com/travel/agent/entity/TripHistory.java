package com.travel.agent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 行程历史实体（用于长期记忆）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripHistory {

    private Long id;

    private Long userId;

    private Long tripId;

    private String tripTitle;

    private String destination;

    private String tripType;

    private String season;

    private String budgetLevel;

    private Double satisfactionScore;

    private String keywords;

    private LocalDateTime createdAt;
}
