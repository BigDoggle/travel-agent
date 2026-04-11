package com.travel.agent.service.ai.impl;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.mapper.ChatMessageMapper;
import com.travel.agent.service.ai.MCPService;
import com.travel.agent.service.memory.MemoryService;
import com.travel.agent.service.memory.SlidingWindowMemory;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 多智能体协调服务实现
 * 委托给 TravelPlannerSupervisor (使用 AgenticServices.supervisorBuilder)
 * 集成记忆功能，为智能体提供个性化上下文
 */
@Slf4j
@Service
public class MCPServiceImpl implements MCPService {

    @Autowired
    private SupervisorAgent supervisorAgent;

    @Autowired
    private MemoryService memoryService;

    @Autowired
    private SlidingWindowMemory slidingWindowMemory;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    // 存储执行状态
    private final ConcurrentMap<String, String> executionStatus = new ConcurrentHashMap<>();

    /**
     * 协调多智能体执行任务
     * 委托给 SupervisorAgent 进行智能体编排
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @param input     用户输入
     * @return 执行结果（Markdown 格式的旅行规划报告）
     */
    @Override
    public String coordinateAgents(Long userId, String sessionId, String input) {
        log.info("协调多智能体执行任务，用户ID: {}, 会话ID: {}", userId, sessionId);

        String sessionKey = userId + "::" + sessionId;

        try {
            executionStatus.put(sessionId, "协调中");

            // 1. 为PlanAgent和ReplanAgent准备记忆上下文
            String planAgentContext = memoryService.buildMemoryContext(userId, sessionId, "plan");
            String replanAgentContext = memoryService.buildMemoryContext(userId, sessionId, "replan");

            // 2. 构建增强的输入
            String enhancedInput = String.format("""
                    用户需求: %s
                    
                    %s
                    
                    %s
                    
                    请基于以上记忆上下文，提供个性化的旅行规划建议。
                    """, input, planAgentContext, replanAgentContext);

            // 3. 保存用户输入到滑动窗口和数据库
            ChatMessage userMessage = createChatMessage(userId, sessionId, "user", input);
            slidingWindowMemory.addMessage(sessionKey, userMessage);
            chatMessageMapper.insert(userMessage);

            // 4. 执行智能体协调
            String finalResponse = supervisorAgent.invoke(enhancedInput);

            // 5. 保存AI响应到滑动窗口和数据库
            ChatMessage aiMessage = createChatMessage(userId, sessionId, "assistant", finalResponse);
            slidingWindowMemory.addMessage(sessionKey, aiMessage);
            chatMessageMapper.insert(aiMessage);

            executionStatus.put(sessionId, "执行完成");
            log.info("协调多智能体执行任务完成，用户ID: {}, 会话ID: {}", userId, sessionId);
            return finalResponse;
        } catch (Exception e) {
            executionStatus.put(sessionId, "执行失败");
            log.error("协调多智能体执行任务失败", e);
            return "协调多智能体执行任务失败，请稍后重试。错误信息：" + e.getMessage();
        }
    }

    private ChatMessage createChatMessage(Long userId, String sessionId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        return message;
    }

    /**
     * 获取当前执行状态
     *
     * @param userId    用户ID
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
     *
     * @param userId    用户ID
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
