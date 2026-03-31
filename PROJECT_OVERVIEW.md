# AI智能旅行规划系统 - 项目框架

## 项目概述

这是一个AI智能旅行规划系统的完整项目框架，包含前后端分离的架构设计。

## 项目结构

```
travel-agent/
├── travel-agent-backend/          # 后端服务 (Spring Boot + LangChain4j)
├── travel-agent-frontend/         # 前端应用 (Vue 3 + TypeScript)
├── .env.example                   # 环境变量示例
└── README.md                      # 项目文档
```

## 快速开始

### 1. 环境准备

- Node.js >= 18.0.0
- Java >= 17
- MySQL >= 8.0
- Redis >= 7.0

### 2. 配置环境变量

```bash
cp .env.example .env
# 编辑.env文件，配置必要的环境变量
```

### 3. 启动后端

```bash
cd travel-agent-backend
mvn spring-boot:run
```

### 4. 启动前端

```bash
cd travel-agent-frontend
npm install
npm run dev
```

## 技术栈

### 后端
- Spring Boot 3.2.0
- LangChain4j 0.32.0
- MySQL 8.0
- Pinecone (向量数据库)
- Redis Cloud (缓存)
- OpenAI API

### 前端
- Vue 3.4
- TypeScript
- Pinia
- Vue Router 4
- Element Plus
- Axios
- Vite
- Tailwind CSS

## 功能特性

- ✅ 智能行程规划
- ✅ 多智能体协作 (Plan-Execute-Replan)
- ✅ 实时信息查询 (天气、交通、地图)
- ✅ RAG增强生成
- ✅ 工具调用
- ✅ MCP协议
- ✅ 两层记忆系统 (Redis + Pinecone)

## 下一步

1. 根据需求完善具体功能实现
2. 配置必要的API密钥
3. 运行项目进行测试
4. 根据实际需求调整配置

## 许可证

MIT License
