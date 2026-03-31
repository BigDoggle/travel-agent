package com.travel.agent.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 行程段实体
 */
@Entity
@Table(name = "trip_segment", indexes = {
    @Index(name = "idx_trip_day", columnList = "trip_id, day_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tripId;

    @Column(nullable = false)
    private Integer dayNumber;

    @Column(length = 100)
    private String location;

    @Column(name = "location_id", length = 100)
    private String locationId;

    @Column(name = "start_time")
    private java.time.LocalTime startTime;

    @Column(name = "end_time")
    private java.time.LocalTime endTime;

    @Column(name = "activity_type", length = 50)
    private String activityType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String address;

    @Column(columnDefinition = "POINT")
    private String coordinates;

    @Column(precision = 2)
    private Double rating;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
