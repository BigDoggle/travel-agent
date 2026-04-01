package com.travel.agent.service.ai;

import com.travel.agent.service.ai.agent.PlanAgent;
import com.travel.agent.service.ai.agent.ExecuteAgent;
import com.travel.agent.service.ai.agent.ReplanAgent;
import java.util.List;
import java.util.Map;

/**
 * MCP服务接口 - 多Agent协调协议
 */
public interface MCPService extends AIService {
    
    /**
     * 协调多Agent执行任务
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param goal 用户目标
     * @param context 上下文信息
     * @return 协调执行结果
     */
    Map<String, Object> coordinateAgents(Long userId, String sessionId, String goal, String context);
    
    /**
     * 分配任务给不同的Agent
     * @param task 任务描述
     * @return Agent分配结果
     */
    String assignTask(String task);
    
    /**
     * 整合多个Agent的执行结果
     * @param results 各Agent的执行结果
     * @return 整合后的结果
     */
    String integrateResults(Map<String, Object> results);
    
    /**
     * 监控Agent执行状态
     * @param agentName Agent名称
     * @return 执行状态
     */
    String monitorAgentStatus(String agentName);
}
