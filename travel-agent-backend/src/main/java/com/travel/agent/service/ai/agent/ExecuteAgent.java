package com.travel.agent.service.ai.agent;

import com.travel.agent.service.ai.AgentService;
import java.util.List;
import java.util.Map;

/**
 * Execute Agent - 执行代理
 */
public interface ExecuteAgent extends AgentService {
    
    /**
     * 执行计划
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param plan 计划步骤列表
     * @return 执行结果
     */
    Map<String, Object> executePlan(Long userId, String sessionId, List<String> plan);
    
    /**
     * 执行单个步骤
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param step 步骤描述
     * @return 步骤执行结果
     */
    String executeStep(Long userId, String sessionId, String step);
}
