package com.travel.agent.service.ai;

import java.util.Map;

/**
 * Tool服务接口
 */
public interface ToolService extends AIService {
    
    /**
     * 调用工具
     * @param toolName 工具名称
     * @param parameters 工具参数
     * @return 工具调用结果
     */
    String callTool(String toolName, Map<String, Object> parameters);
    
    /**
     * 获取可用工具列表
     * @return 可用工具列表
     */
    Map<String, String> getAvailableTools();
    
    /**
     * 调用地图工具
     * @param location 位置
     * @return 地图信息
     */
    String callMapTool(String location);
    
    /**
     * 调用交通工具
     * @param from 出发地
     * @param to 目的地
     * @return 交通信息
     */
    String callTransportationTool(String from, String to);
    
    /**
     * 调用天气工具
     * @param location 位置
     * @return 天气信息
     */
    String callWeatherTool(String location);
}
