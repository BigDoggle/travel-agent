package com.travel.agent.service.ai;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Plan Agent 的决策输出
 */
@Data
@NoArgsConstructor
public class PlanDecision {
    
    @Description("决策类型：PLAN(制定计划)、EXECUTE(执行步骤)、FINISH(生成最终报告)")
    private String decision;
    
    @Description("当前步骤的描述或最终报告的标题")
    private String step;
    
    @Description("预期要调用的工具列表，逗号分隔，如 'weather,map,transportation'")
    private String expectedTools;
    
    @Description("必要的上下文信息，用于传递给 Execute Agent")
    private String context;
    
    @Description("执行结果的反馈摘要（仅在 EXECUTE 阶段使用）")
    private String executionFeedback;
}
