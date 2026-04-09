# 项目指南

## 当前任务
- 后端：travel-agent-backend (Java Spring Boot, MyBatis重构)
- AI聊天功能实现
- LangChain4j Supervisor协调智能体

## 压缩指令
- 始终保留：文件路径、当前重构进度、测试状态、关键接口定义
- 可以压缩：工具输出结果、文件内容详情、API响应详情

## 上下文管理规则
- 当上下文达到70%时主动使用/compact
- 每个子任务完成后进行压缩
- 使用子代理处理复杂任务
