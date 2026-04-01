package com.travel.agent.service.ai.agent;

import com.travel.agent.service.ai.AgentService;
import java.util.List;

/**
 * Plan Agent - 计划生成代理
 */
public interface PlanAgent extends AgentService {
    
    /**
     * 生成执行计划
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param goal 用户目标
     * @param context 上下文信息
     * @return 计划步骤列表
     */
    List<String> generatePlan(Long userId, String sessionId, String goal, String context);
    
    /**
     * 评估计划可行性
     * @param plan 计划步骤列表
     * @return 可行性评估结果
     */
    boolean evaluatePlan(List<String> plan);
}
