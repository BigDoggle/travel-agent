package com.travel.agent.service.memory.impl;

import com.travel.agent.entity.ChatMessage;
import com.travel.agent.entity.MemorySummary;
import com.travel.agent.entity.UserPreference;
import com.travel.agent.mapper.ChatMessageMapper;
import com.travel.agent.mapper.MemorySummaryMapper;
import com.travel.agent.mapper.UserPreferenceMapper;
import com.travel.agent.service.memory.MemoryService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MemoryServiceImpl implements MemoryService {

    private final OpenAiChatModel chatModel;
    private final UserPreferenceMapper userPreferenceMapper;
    private final MemorySummaryMapper memorySummaryMapper;
    private final ChatMessageMapper chatMessageMapper;

    public MemoryServiceImpl(OpenAiChatModel chatModel,
                             UserPreferenceMapper userPreferenceMapper,
                             MemorySummaryMapper memorySummaryMapper,
                             ChatMessageMapper chatMessageMapper) {
        this.chatModel = chatModel;
        this.userPreferenceMapper = userPreferenceMapper;
        this.memorySummaryMapper = memorySummaryMapper;
        this.chatMessageMapper = chatMessageMapper;
    }

    @Override
    public String generateConversationSummary(List<ChatMessage> messages) {
        try {
            // 使用大模型生成智能摘要
            String conversationText = formatMessagesForSummary(messages);

            String prompt = String.format("""
                    你是一个专业记忆摘要生成agent，需要分析用户对话并生成简洁的摘要。
                    
                    对话内容：
                    %s
                    
                    请生成一个简洁的对话摘要，包含以下要点：
                    1. 用户的主要需求和偏好
                    2. 讨论的关键主题（如目的地、预算、旅行风格等）
                    3. 重要的决策或计划
                    4. 其他与用户相关的信息
                    
                    摘要要求：
                    - 简洁明了
                    - 突出用户的旅行偏好和需求
                    - 包含关键信息点
                    - 使用客观、准确的描述
                    
                    请直接输出摘要内容，不要包含其他说明。
                    """, conversationText);

            String summary = chatModel.chat(prompt);
            log.info("大模型生成对话摘要: {}", summary);
            return summary.trim();

        } catch (Exception e) {
            log.error("大模型生成摘要失败，使用备用方法", e);
            return generateFallbackSummary(messages);
        }
    }

    // 备用摘要生成方法（当大模型失败时使用）
    private String generateFallbackSummary(List<ChatMessage> messages) {
        StringBuilder summary = new StringBuilder();

        long userMessageCount = messages.stream()
                .filter(msg -> "user".equals(msg.getRole()))
                .count();

        if (userMessageCount > 0) {
            summary.append("对话包含 ").append(userMessageCount).append(" 条用户消息，");

            String allContent = messages.stream()
                    .map(ChatMessage::getContent)
                    .collect(Collectors.joining(" "));

            if (allContent.contains("文化") || allContent.contains("历史")) {
                summary.append("用户偏好文化旅游，");
            }
            if (allContent.contains("预算") || allContent.contains("价格")) {
                summary.append("涉及预算讨论，");
            }
            if (allContent.contains("美食") || allContent.contains("餐厅")) {
                summary.append("关注美食体验，");
            }

            summary.append("主要讨论旅行规划相关内容。");
        } else {
            summary.append("暂无有效对话内容。");
        }

        log.info("备用方法生成对话摘要: {}", summary.toString());
        return summary.toString();
    }

    private String formatMessagesForSummary(List<ChatMessage> messages) {
        return messages.stream()
                .map(msg -> String.format("%s: %s", msg.getRole(), msg.getContent()))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public List<UserPreference> extractUserPreferences(List<ChatMessage> messages) {
        try {
            String conversationText = formatMessagesForSummary(messages);

            String prompt = String.format("""
                    分析以下用户对话，提取用户的旅行偏好信息。
                    
                    对话内容：
                    %s
                    
                    请提取以下类型的偏好信息（如果存在）：
                    1. 旅行风格偏好（如：文化旅游、探险旅游、休闲度假等）
                    2. 预算水平（如：经济型、中等、豪华等）
                    3. 目的地偏好（如：海滨、山区、城市等）
                    4. 季节偏好（如：春季、夏季、秋季、冬季）
                    5. 报告详细程度偏好（如：简洁、详细、专业等）
                    
                    请以JSON格式返回提取的偏好信息，格式如下：
                    {
                      "preferences": [
                        {
                          "type": "travel_style",
                          "value": "文化旅游",
                          "confidence": 0.8
                        }
                      ]
                    }
                    
                    只返回JSON数据，不要包含其他说明。
                    """, conversationText);

            String jsonResponse = chatModel.chat(prompt);
            List<UserPreference> preferences = parsePreferencesFromJson(jsonResponse);
            log.info("大模型提取用户偏好: {} 项", preferences.size());
            return preferences;

        } catch (Exception e) {
            log.error("大模型提取偏好失败，使用备用方法", e);
            return extractFallbackPreferences(messages);
        }
    }

    private List<UserPreference> parsePreferencesFromJson(String jsonResponse) {
        List<UserPreference> preferences = new ArrayList<>();

        try {
            // 清理JSON响应（移除可能的markdown代码块标记）
            String cleanJson = jsonResponse.trim();
            if (cleanJson.startsWith("`")) {
                cleanJson = cleanJson.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(cleanJson);
            JsonNode preferencesNode = rootNode.get("preferences");

            if (preferencesNode != null && preferencesNode.isArray()) {
                for (JsonNode prefNode : preferencesNode) {
                    UserPreference pref = new UserPreference();
                    pref.setPreferenceType(prefNode.get("type").asText());
                    pref.setPreferenceValue(prefNode.get("value").asText());
                    pref.setConfidence(prefNode.get("confidence").asDouble());
                    pref.setSource("AI分析");
                    preferences.add(pref);
                }
            }
        } catch (Exception e) {
            log.error("解析偏好JSON失败: {}", jsonResponse, e);
        }

        return preferences;
    }

    private List<UserPreference> extractFallbackPreferences(List<ChatMessage> messages) {
        List<UserPreference> preferences = new ArrayList<>();
        String allContent = messages.stream()
                .filter(msg -> "user".equals(msg.getRole()))
                .map(ChatMessage::getContent)
                .collect(Collectors.joining(" "));

        // 提取旅行风格偏好
        if (allContent.contains("文化") || allContent.contains("历史")) {
            UserPreference pref = new UserPreference();
            pref.setPreferenceType("travel_style");
            pref.setPreferenceValue("文化旅游");
            pref.setConfidence(0.8);
            pref.setSource("关键词分析");
            preferences.add(pref);
        }

        // 提取预算偏好
        if (allContent.contains("中等预算") || allContent.contains("中等价格")) {
            UserPreference pref = new UserPreference();
            pref.setPreferenceType("budget_level");
            pref.setPreferenceValue("中等");
            pref.setConfidence(0.7);
            pref.setSource("关键词分析");
            preferences.add(pref);
        }

        // 提取报告详细程度偏好
        if (allContent.contains("详细") || allContent.contains("具体")) {
            UserPreference pref = new UserPreference();
            pref.setPreferenceType("report_detail");
            pref.setPreferenceValue("详细");
            pref.setConfidence(0.6);
            pref.setSource("关键词分析");
            preferences.add(pref);
        }

        log.info("备用方法提取用户偏好: {} 项", preferences.size());
        return preferences;
    }

    @Override
    public void saveConversationSummary(Long userId, String sessionId, String summary) {
        try {
            // 检查是否已存在该用户+会话的摘要
            MemorySummary existing = memorySummaryMapper.findByUserSessionAndType(
                    userId, sessionId, "conversation");

            MemorySummary memorySummary = new MemorySummary();
            memorySummary.setUserId(userId);
            memorySummary.setSessionId(sessionId);
            memorySummary.setSummaryType("conversation");
            memorySummary.setContent(summary);
            LocalDateTime now = LocalDateTime.now();

            if (existing != null) {
                // 更新现有摘要（覆盖模式）
                memorySummary.setId(existing.getId());
                memorySummary.setCreatedAt(existing.getCreatedAt());
                memorySummary.setUpdatedAt(now);
                memorySummaryMapper.update(memorySummary);
                log.info("更新对话摘要成功，用户ID: {}, 会话ID: {}", userId, sessionId);
            } else {
                // 创建新摘要
                memorySummary.setCreatedAt(now);
                memorySummary.setUpdatedAt(now);
                memorySummaryMapper.insert(memorySummary);
                log.info("创建对话摘要成功，用户ID: {}, 会话ID: {}", userId, sessionId);
            }
        } catch (Exception e) {
            log.error("保存对话摘要失败", e);
        }
    }

    @Override
    public void saveUserPreference(Long userId, UserPreference preference) {
        try {
            preference.setUserId(userId);
            LocalDateTime now = LocalDateTime.now();
            preference.setCreatedAt(now);
            preference.setUpdatedAt(now);

            // 检查是否已存在相同类型的偏好
            UserPreference existing = userPreferenceMapper.findByUserIdAndType(userId, preference.getPreferenceType());
            if (existing != null) {
                // 更新现有偏好（如果新偏好置信度更高）
                if (preference.getConfidence() > existing.getConfidence()) {
                    preference.setId(existing.getId());
                    preference.setCreatedAt(existing.getCreatedAt());
                    userPreferenceMapper.update(preference);
                }
            } else {
                userPreferenceMapper.insert(preference);
            }

            log.info("保存用户偏好成功，用户ID: {}, 类型: {}", userId, preference.getPreferenceType());
        } catch (Exception e) {
            log.error("保存用户偏好失败", e);
        }
    }

    @Override
    public List<UserPreference> getUserPreferences(Long userId) {
        return userPreferenceMapper.findByUserId(userId);
    }

    @Override
    public String getLatestConversationSummary(Long userId, String sessionId) {
        MemorySummary summary = memorySummaryMapper.findByUserSessionAndType(userId, sessionId, "conversation");
        return summary != null ? summary.getContent() : "";
    }

    @Override
    public String buildMemoryContext(Long userId, String sessionId, String agentType) {
        StringBuilder context = new StringBuilder();

        // 1. 获取用户偏好
        List<UserPreference> preferences = getUserPreferences(userId);

        // 2. 获取对话摘要
        String conversationSummary = getLatestConversationSummary(userId, sessionId);

        // 3. 获取最近的对话消息（用于补充上下文）
        List<ChatMessage> recentMessages = chatMessageMapper.findRecentByUserSession(userId, sessionId, 5);

        switch (agentType.toLowerCase()) {
            case "plan":
                // PlanAgent：提供用户偏好 + 对话摘要
                if (!conversationSummary.isEmpty()) {
                    context.append("## 对话摘要\\n")
                            .append(conversationSummary).append("\\n\\n");
                }

                context.append("## 用户偏好信息\\n");
                preferences.stream()
                        .filter(pref -> isPlanRelevantPreference(pref.getPreferenceType()))
                        .forEach(pref -> context.append("- ")
                                .append(mapPreferenceType(pref.getPreferenceType()))
                                .append(": ")
                                .append(pref.getPreferenceValue())
                                .append("\\n"));
                break;

            case "replan":
                // ReplanAgent：提供报告相关偏好 + 对话摘要
                if (!conversationSummary.isEmpty()) {
                    context.append("## 对话摘要\\n")
                            .append(conversationSummary).append("\\n\\n");
                }

                context.append("## 报告偏好\\n");
                preferences.stream()
                        .filter(pref -> isReportRelevantPreference(pref.getPreferenceType()))
                        .forEach(pref -> context.append("- ")
                                .append(mapPreferenceType(pref.getPreferenceType()))
                                .append(": ")
                                .append(pref.getPreferenceValue())
                                .append("\\n"));
                break;

            case "fast":
                // FastAgent：提供完整的记忆上下文（用户偏好 + 对话摘要 + 最近对话）
                if (!conversationSummary.isEmpty()) {
                    context.append("## 对话摘要\\n")
                            .append(conversationSummary).append("\\n\\n");
                }

                context.append("## 用户偏好信息\\n");
                preferences.stream()
                        .filter(pref -> isPlanRelevantPreference(pref.getPreferenceType()) ||
                                isReportRelevantPreference(pref.getPreferenceType()))
                        .forEach(pref -> context.append("- ")
                                .append(mapPreferenceType(pref.getPreferenceType()))
                                .append(": ")
                                .append(pref.getPreferenceValue())
                                .append("\\n"));

                // 添加最近对话上下文
                if (!recentMessages.isEmpty()) {
                    context.append("\\n## 最近对话\\n");
                    recentMessages.forEach(msg -> context.append("- ")
                            .append(msg.getRole()).append(": ")
                            .append(msg.getContent()).append("\\n"));
                }
                break;

            default:
                // ExecuteAgent或其他：提供基本信息
                if (!conversationSummary.isEmpty()) {
                    context.append(conversationSummary);
                }
                break;
        }

        return context.toString();
    }

    private boolean isPlanRelevantPreference(String type) {
        return Arrays.asList("travel_style", "budget_level", "preferred_season", "destination_type").contains(type);
    }

    private boolean isReportRelevantPreference(String type) {
        return Arrays.asList("report_detail", "report_focus", "report_format").contains(type);
    }

    private String formatMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(msg -> String.format("%s: %s", msg.getRole(), msg.getContent()))
                .collect(Collectors.joining("\\n"));
    }

    private String mapPreferenceType(String type) {
        Map<String, String> mapping = Map.of(
                "travel_style", "旅行风格",
                "budget_level", "预算水平",
                "preferred_season", "偏好季节",
                "destination_type", "目的地类型",
                "report_detail", "报告详细程度",
                "report_focus", "报告关注重点",
                "report_format", "报告格式偏好"
        );
        return mapping.getOrDefault(type, type);
    }
}