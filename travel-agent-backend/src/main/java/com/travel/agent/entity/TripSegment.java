package com.travel.agent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 行程段实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripSegment {

    private Long id;

    private Long tripId;

    private Integer dayNumber;

    private String location;

    private String locationId;

    private LocalTime startTime;

    private LocalTime endTime;

    private String activityType;

    private String description;

    private String address;

    private String coordinates;

    private Double rating;

    private LocalDateTime createdAt;
}
