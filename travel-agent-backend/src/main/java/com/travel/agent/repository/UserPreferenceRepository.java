package com.travel.agent.repository;

import com.travel.agent.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 用户偏好仓储接口
 */
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    List<UserPreference> findByUserId(Long userId);

    List<UserPreference> findByUserIdAndPreferenceType(Long userId, String preferenceType);
}
