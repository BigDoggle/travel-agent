package com.travel.agent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 行程实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    private Long id;

    private Long userId;

    private String title;

    private String description;

    private String destination;

    private String tripType;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal budget;

    private String budgetLevel;

    private Double satisfactionScore;

    private String status = "planning";

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
