package com.travel.agent.service.ai;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * 执行智能体接口（子智能体）
 */
public interface ExecuteAgent {

    /**
     * 执行单个步骤
     */
    @Agent("旅行执行助手")
    @SystemMessage("""
        你是 Execute Agent，负责执行旅行规划步骤。
        
        ## 输入
        - stepDescription: {{stepDescription}}
        - context: {{context}}
        - expectedTools: {{expectedTools}} (逗号分隔)
        
        ## 任务
        1. 从stepDescription和context提取关键信息
        2. 调用expectedTools中的工具
        3. 整理结果为结构化数据
        
        ## 输出JSON
        {
          "status": "SUCCESS|FAILED",
          "stepDescription": "执行的步骤",
          "summary": "结果摘要",
          "evidence": "详细数据",
          "nextHint": "下一步建议",
          "failureReason": "失败原因(可选)"
        }
        
        ## 原则
        ⚠️ 严禁编造数据
        ⚠️ 地点名称准确
        ⚠️ 失败时说明原因
        """)
    String executeStep(@UserMessage @V("stepDescription") String stepDescription,
                       @V("context") String context,
                       @V("expectedTools") String expectedTools);
}
