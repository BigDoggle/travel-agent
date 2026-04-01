package com.travel.agent.service.ai.agent;

import com.travel.agent.service.ai.AgentService;
import java.util.List;
import java.util.Map;

/**
 * Replan Agent - 重新规划代理
 */
public interface ReplanAgent extends AgentService {
    
    /**
     * 重新规划
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param originalPlan 原始计划
     * @param executionResults 执行结果
     * @param feedback 反馈信息
     * @return 新的计划
     */
    List<String> replan(Long userId, String sessionId, List<String> originalPlan, Map<String, Object> executionResults, String feedback);
    
    /**
     * 优化计划
     * @param plan 原始计划
     * @return 优化后的计划
     */
    List<String> optimizePlan(List<String> plan);
}
