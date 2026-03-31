package com.travel.agent.repository;

import com.travel.agent.entity.TripHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 行程历史仓储接口（用于长期记忆）
 */
public interface TripHistoryRepository extends JpaRepository<TripHistory, Long> {

    List<TripHistory> findByUserId(Long userId);

    List<TripHistory> findByUserIdAndTripType(Long userId, String tripType);
}
