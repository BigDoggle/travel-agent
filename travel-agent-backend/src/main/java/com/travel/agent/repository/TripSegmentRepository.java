package com.travel.agent.repository;

import com.travel.agent.entity.TripSegment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 行程段仓储接口
 */
public interface TripSegmentRepository extends JpaRepository<TripSegment, Long> {

    List<TripSegment> findByTripId(Long tripId);

    List<TripSegment> findByTripIdOrderByDayNumberAsc(Long tripId);
}
