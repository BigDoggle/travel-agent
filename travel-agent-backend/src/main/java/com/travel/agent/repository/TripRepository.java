package com.travel.agent.repository;

import com.travel.agent.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 行程仓储接口
 */
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByUserId(Long userId);

    List<Trip> findByUserIdAndStatus(Long userId, String status);

    List<Trip> findByStartDateAfter(java.time.LocalDate date);
}
