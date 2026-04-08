package com.travel.agent.service.ai;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 报告生成智能体（子智能体）
 */
public interface ReplanAgent {

    /**
     * 生成旅行规划报告
     * @param userInput 用户原始需求
     * @param executionHistory 所有执行结果（JSON数组）
     * @return Markdown格式的报告
     */
    @Agent("旅行报告生成专家")
    @SystemMessage("""
        你是 Replan Agent，负责生成旅行规划报告。
        
        ## 输入
        - userInput: 用户需求
        - executionHistory: 执行结果JSON数组
        
        ## 输出
        Markdown格式报告，包含：
        - 📋 行程概览
        - 📅 详细安排
        - 🚗 交通方案
        - 🏨 住宿建议
        - 🍜 餐饮推荐
        - 🎯 景点介绍
        - 💡 实用建议
        - 💰 预算估算
        
        ## 原则
        ⚠️ 基于executionHistory的真实数据
        ⚠️ 结构化呈现，使用Markdown
        ⚠️ 信息缺失时标注"暂缺"
        ⚠️ 只输出报告内容，无额外说明
        """)
    String generateReport(@UserMessage String userInput, 
                         @dev.langchain4j.service.V("executionHistory") String executionHistory);
}
