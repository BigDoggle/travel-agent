package com.travel.agent.service.ai.impl;

import com.travel.agent.service.ai.MCPService;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 多智能体协调服务实现
 * 委托给 TravelPlannerSupervisor (使用 AgenticServices.supervisorBuilder)
 */
@Slf4j
@Service
public class MCPServiceImpl implements MCPService {
    
    @Autowired
    private SupervisorAgent supervisorAgent;

    // 存储执行状态
    private final ConcurrentMap<String, String> executionStatus = new ConcurrentHashMap<>();

    /**
     * 协调多智能体执行任务
     * 委托给 SupervisorAgent 进行智能体编排
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param input 用户输入
     * @return 执行结果（Markdown 格式的旅行规划报告）
     */
    @Override
    public String coordinateAgents(Long userId, String sessionId, String input) {
        log.info("协调多智能体执行任务，用户ID: {}, 会话ID: {}", userId, sessionId);

        try {
            executionStatus.put(sessionId, "协调中");

            // 委托给 SupervisorAgent，它会自动调度 PlanAgent、ExecuteAgent 和 ReplanAgent
            String finalResponse = supervisorAgent.invoke(input);

            executionStatus.put(sessionId, "执行完成");
            log.info("协调多智能体执行任务完成，用户ID: {}, 会话ID: {}", userId, sessionId);
            return finalResponse;
        } catch (Exception e) {
            executionStatus.put(sessionId, "执行失败");
            log.error("协调多智能体执行任务失败", e);
            return "协调多智能体执行任务失败，请稍后重试。错误信息：" + e.getMessage();
        }
    }

    /**
     * 获取当前执行状态
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 执行状态
     */
    @Override
    public String getExecutionStatus(Long userId, String sessionId) {
        log.info("获取当前执行状态，用户ID: {}, 会话ID: {}", userId, sessionId);

        try {
            String status = executionStatus.getOrDefault(sessionId, "未执行");
            log.info("获取当前执行状态完成，用户ID: {}, 会话ID: {}, 状态: {}", userId, sessionId, status);
            return status;
        } catch (Exception e) {
            log.error("获取当前执行状态失败", e);
            return "获取当前执行状态失败，请稍后重试。";
        }
    }

    /**
     * 取消执行任务
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 取消结果
     */
    @Override
    public String cancelExecution(Long userId, String sessionId) {
        log.info("取消执行任务，用户ID: {}, 会话ID: {}", userId, sessionId);

        try {
            executionStatus.put(sessionId, "已取消");
            log.info("取消执行任务完成，用户ID: {}, 会话ID: {}", userId, sessionId);
            return "执行任务已取消";
        } catch (Exception e) {
            log.error("取消执行任务失败", e);
            return "取消执行任务失败，请稍后重试。";
        }
    }
}
