package com.travel.agent.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Agent 集成测试
 * 测试 TravelPlannerSupervisor 协调 PlanAgent 和 ExecuteAgent 的完整流程
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class MCPServiceIntegrationTest {

    @Autowired
    private MCPService mcpService;

    /**
     * 测试北京一日游规划场景
     * 验证 Supervisor 能否正确协调 PlanAgent 和 ExecuteAgent
     */
    @Test
    public void testBeijingOneDayTourPlanning() {
        log.info("========== 开始测试北京一日游规划场景 ==========");

        // 模拟用户ID和会话ID
        Long userId = 1L;
        String sessionId = "beijing-tour-test-" + System.currentTimeMillis();
        String userInput = "生成一个天津三日游";

        log.info("用户ID: {}, 会话ID: {}", userId, sessionId);
        log.info("用户需求: {}", userInput);

        // 调用多智能体协调服务
        String result = mcpService.coordinateAgents(userId, sessionId, userInput);

        // 验证结果
        assertNotNull(result, "协调服务返回结果不应为空");
        assertFalse(result.isEmpty(), "协调服务返回结果不应为空字符串");
        assertFalse(result.contains("失败"), "协调服务不应返回失败消息: " + result);
        assertFalse(result.contains("错误"), "协调服务不应返回错误消息: " + result);

        log.info("✅ 协调服务执行成功");
        log.info("返回结果长度: {} 字符", result.length());
        log.info("结果预览:\n{}", result.substring(0, Math.min(500, result.length())));

        log.info("========== 北京一日游规划场景测试完成 ==========");
    }

    /**
     * 测试执行状态查询
     */
    @Test
    public void testGetExecutionStatus() {
        log.info("========== 开始测试执行状态查询 ==========");

        Long userId = 2L;
        String sessionId = "status-test-" + System.currentTimeMillis();

        // 执行一个任务
        String userInput = "为我规划一个上海一日游";
        String result = mcpService.coordinateAgents(userId, sessionId, userInput);
        assertNotNull(result, "协调服务返回结果不应为空");
        log.info("✅ 任务执行完成");

        // 查询执行状态
        String status = mcpService.getExecutionStatus(userId, sessionId);
        assertNotNull(status, "执行状态不应为空");
        log.info("执行状态: {}", status);

        // 验证状态应该是"执行完成"或类似状态
        assertTrue(status.contains("完成") || status.contains("协调中") ||
                  status.contains("未执行") || status.contains("已取消"),
                  "执行状态应包含有效状态信息: " + status);

        log.info("✅ 执行状态查询测试完成");
        log.info("========== 执行状态查询测试完成 ==========");
    }

    /**
     * 测试任务取消
     */
    @Test
    public void testCancelExecution() {
        log.info("========== 开始测试任务取消 ==========");

        Long userId = 3L;
        String sessionId = "cancel-test-" + System.currentTimeMillis();

        // 取消一个尚未执行的任务
        String cancelResult = mcpService.cancelExecution(userId, sessionId);
        assertNotNull(cancelResult, "取消结果不应为空");
        log.info("取消结果: {}", cancelResult);

        // 验证状态
        String status = mcpService.getExecutionStatus(userId, sessionId);
        assertNotNull(status, "执行状态不应为空");
        log.info("取消后的执行状态: {}", status);

        log.info("✅ 任务取消测试完成");
        log.info("========== 任务取消测试完成 ==========");
    }
}