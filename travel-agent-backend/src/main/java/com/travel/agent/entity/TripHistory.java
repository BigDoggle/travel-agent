package com.travel.agent.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 行程历史实体（用于长期记忆）
 */
@Entity
@Table(name = "trip_history", indexes = {
    @Index(name = "idx_user_trip", columnList = "user_id, trip_id"),
    @Index(name = "idx_user_type", columnList = "user_id, trip_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long tripId;

    @Column(name = "trip_title", length = 200)
    private String tripTitle;

    @Column(length = 100)
    private String destination;

    @Column(name = "trip_type", length = 50)
    private String tripType;

    @Column(length = 20)
    private String season;

    @Column(name = "budget_level", length = 20)
    private String budgetLevel;

    @Column(name = "satisfaction_score", precision = 2, scale = 1)
    private Double satisfactionScore;

    @Column(columnDefinition = "JSON")
    private String keywords;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
