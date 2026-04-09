package com.travel.agent.config;

import com.travel.agent.service.ai.ExecuteAgent;
import com.travel.agent.service.ai.PlanAgent;
import com.travel.agent.service.ai.ReplanAgent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorContextStrategy;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * AI 配置类
 * 负责创建和管理 LangChain4j Agent Bean
 */
@Configuration
public class AIConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.base-url}")
    private String openaiBaseUrl;

    @Value("${openai.model}")
    private String openaiModel;

    @Value("${openai.timeout:60}")
    private int openaiTimeoutSeconds;

    /**
     * 创建 OpenAI 聊天模型 Bean
     */
    @Bean
    public OpenAiChatModel chatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(openaiBaseUrl)
                .apiKey(openaiApiKey)
                .modelName(openaiModel)
                .timeout(Duration.ofSeconds(openaiTimeoutSeconds))
                .temperature(0.7)  // 默认温度
                .build();
    }

    /**
     * 创建 PlanAgent Bean（计划智能体）
     * 使用 AgenticServices.agentBuilder 构建
     */
    @Bean
    public PlanAgent planAgent(OpenAiChatModel chatModel) {
        return AgenticServices
                .agentBuilder(PlanAgent.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 创建 ExecuteAgent Bean（执行智能体）
     * 使用 AgenticServices.agentBuilder 构建
     */
    @Bean
    public ExecuteAgent executeAgent(OpenAiChatModel chatModel) {
        return AgenticServices
                .agentBuilder(ExecuteAgent.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 创建 ReplanAgent Bean（报告生成智能体）
     * 使用 AgenticServices.agentBuilder 构建
     * 专门负责生成最终的 Markdown 格式旅行规划报告
     */
    @Bean
    public ReplanAgent replanAgent(OpenAiChatModel chatModel) {
        return AgenticServices
                .agentBuilder(ReplanAgent.class)
                .chatModel(chatModel)
                .build();
    }

    @Bean
    public SupervisorAgent supervisorAgent(PlanAgent planAgent, ExecuteAgent executeAgent, ReplanAgent replanAgent, OpenAiChatModel chatModel) {
        return AgenticServices
                .supervisorBuilder(SupervisorAgent.class)
                .chatModel(chatModel)
                .subAgents(planAgent, executeAgent, replanAgent)  // 注册三个子智能体
                .contextGenerationStrategy(SupervisorContextStrategy.CHAT_MEMORY_AND_SUMMARIZATION)
                .responseStrategy(SupervisorResponseStrategy.LAST)
                .supervisorContext("""
        你是 Travel Planner Supervisor，负责调度三个子智能体。
        
        ## 工作流程
        1. 调用 plan_agent.makeDecision(input="TASK: {需求}\nFEEDBACK: ")
        2. 如果 decision=PLAN/EXECUTE → 调用 execute_agent.executeStep()
        3. 将执行结果反馈给 plan_agent（再次调用 makeDecision）
        4. 重复步骤2-3，直到 decision=FINISH
        5. 调用 replan_agent.generateReport() 生成最终报告
        
        ## 原则
        ⚠️ 每次 ExecuteAgent 执行后必须反馈给 PlanAgent
        ⚠️ 只有 PlanAgent 能决定何时结束（decision=FINISH）
        ⚠️ FINISH 后调用 ReplanAgent 生成报告
        ⚠️ 你只负责调度，不生成内容
        ⚠️ 最终的结果必须由replanAgent输出
        """)
                .build();
    }
}
