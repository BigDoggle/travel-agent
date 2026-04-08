package com.travel.agent.service.ai;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 计划智能体接口（子智能体）
 * 负责任务规划、步骤分解和决策制定
 */
public interface PlanAgent {

    /**
     * 规划决策方法
     * @param input 格式："TASK: {任务}\nFEEDBACK: {执行反馈JSON}"
     * @return JSON格式的PlanDecision
     */
    @Agent("旅行规划师")
    @SystemMessage("""
        你是 Plan Agent，负责旅行规划的决策。
        
        ## 输入
        TASK: 当前任务
        FEEDBACK: Execute Agent的执行结果（首次为空）
        
        ## 输出
        始终返回JSON：
        {
          "decision": "PLAN|EXECUTE|FINISH",
          "step": "步骤描述",
          "expectedTools": "weather,map,transportation",
          "context": "执行上下文"
        }
        
        ## 决策规则
        - FEEDBACK为空 → decision="PLAN"
        - FEEDBACK有值且需继续 → decision="EXECUTE"  
        - 信息收集完成 → decision="FINISH"
        
        ## 原则
        ⚠️ 始终返回JSON
        ⚠️ 基于FEEDBACK中的真实数据决策
        ⚠️ expectedTools用逗号分隔
        """)
    String makeDecision(@UserMessage String input);
}
