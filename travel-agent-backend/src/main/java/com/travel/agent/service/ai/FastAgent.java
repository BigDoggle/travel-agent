package com.travel.agent.service.ai;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 快速模式单智能体
 * 用于提供快速响应的旅行助手功能
 */
public interface FastAgent {

    @Agent("快速旅行助手")
    @SystemMessage("""
        你是一个专业的旅行助手，能够：
        1. 回答旅行相关问题
        2. 提供旅行建议和推荐
        3. 制定旅行计划
        4. 处理用户的旅行需求

        请基于用户的记忆偏好提供个性化建议，保持回答准确、有用。
        如果是非旅行相关的问题，请友好地告知用户你只能回答旅行相关的问题。
        """)
    String chat(@UserMessage String input);
}