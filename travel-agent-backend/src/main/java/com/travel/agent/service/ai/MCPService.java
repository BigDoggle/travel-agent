package com.travel.agent.service.ai;

/**
 * 多智能体协调服务接口
 * 负责协调PlanAgent、ExecuteAgent和ReplanAgent的工作，实现Plan-execute-replan架构
 */
public interface MCPService extends AIService {
    
    /**
     * 协调多智能体执行任务
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param input 用户输入
     * @return 执行结果
     */
    String coordinateAgents(Long userId, String sessionId, String input);
    
    /**
     * 获取当前执行状态
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 执行状态
     */
    String getExecutionStatus(Long userId, String sessionId);
    
    /**
     * 取消执行任务
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 取消结果
     */
    String cancelExecution(Long userId, String sessionId);
}
