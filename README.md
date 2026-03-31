# AI智能旅行规划系统

基于AI技术的智能旅行规划系统，提供个性化、智能化、全方位的旅行规划服务。

## 📋 目录

- [项目简介](#项目简介)
- [技术架构](#技术架构)
- [功能特性](#功能特性)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [开发指南](#开发指南)
- [部署说明](#部署说明)
- [技术栈](#技术栈)

## 🎯 项目简介

AI智能旅行规划系统是一个基于人工智能技术的旅行规划平台，主要特点：

- **智能行程规划**：基于用户需求自动生成个性化旅行计划
- **多智能体协作**：Plan-Execute-Replan循环机制
- **实时信息查询**：天气、交通、地图等实时信息
- **知识库检索**：RAG增强生成技术
- **工具调用**：多种实用工具集成
- **记忆管理**：短期记忆(Redis) + 长期记忆(Pinecone)

## 🏗️ 技术架构

### 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层 (Vue 3)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │  用户界面   │  │  地图导航   │  │  个人中心   │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      后端服务层 (Spring Boot)                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │ 用户服务    │  │ 行程服务    │  │ 收藏服务    │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐           │
│  │ AI服务      │  │ MCP服务     │  │ 消息服务    │           │
│  └─────────────┘  └─────────────┘  └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Pinecone   │    │   Redis     │    │   MySQL     │
│  (长期记忆)  │    │ (短期记忆)  │    │ (业务数据)  │
└─────────────┘    └─────────────┘    └─────────────┘
```

### 技术栈

| 层级 | 技术 | 说明 |
|-----|------|------|
| **前端** | Vue 3 + TypeScript | 响应式前端框架 |
| **状态管理** | Pinia | Vue 3状态管理 |
| **路由** | Vue Router 4 | 前端路由 |
| **UI框架** | Element Plus | UI组件库 |
| **HTTP客户端** | Axios | HTTP请求 |
| **后端** | Spring Boot 3.x | Java后端框架 |
| **AI框架** | LangChain4j | AI集成框架 |
| **数据库** | MySQL 8.0 | 关系型数据库 |
| **向量数据库** | Pinecone | 长期记忆存储 |
| **缓存** | Redis Cloud | 短期记忆存储 |
| **构建工具** | Vite | 前端构建工具 |

## ✨ 功能特性

### 核心功能

- ✅ **智能行程规划** - 基于用户需求自动生成个性化旅行计划
- ✅ **多智能体协作** - Plan-Execute-Replan循环机制
- ✅ **实时信息查询** - 天气、交通、地图等实时信息
- ✅ **RAG增强生成** - 基于知识库的智能回答
- ✅ **工具调用** - 天气、交通、地图等工具集成
- ✅ **MCP协议** - 地图导航、天气显示、交通查询
- ✅ **两层记忆系统** - Redis短期记忆 + Pinecone长期记忆

### 用户功能

- ✅ 用户注册/登录/登出
- ✅ 个人信息管理
- ✅ 行程创建/编辑/删除
- ✅ 行程规划生成
- ✅ 行程历史查看
- ✅ 个人偏好设置

## 📁 项目结构

```
travel-agent/
├── travel-agent-backend/          # 后端服务
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/travel/agent/
│   │   │   │   ├── config/        # 配置类
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── service/       # 服务层
│   │   │   │   │   ├── ai/        # AI服务
│   │   │   │   │   └── agent/     # Agent服务
│   │   │   │   ├── repository/    # 数据访问
│   │   │   │   ├── entity/        # 实体
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   └── exception/     # 异常处理
│   │   │   └── resources/
│   │   │       ├── application.yml      # 主配置
│   │   │       ├── application-dev.yml  # 开发配置
│   │   │       ├── application-prod.yml # 生产配置
│   │   │       └── db/
│   │   │           └── schema.sql       # 数据库初始化
│   │   └── test/
│   │       └── java/com/travel/agent/
│   └── pom.xml                    # Maven配置
│
├── travel-agent-frontend/         # 前端应用
│   ├── src/
│   │   ├── assets/                # 静态资源
│   │   ├── components/            # 组件
│   │   │   ├── common/           # 通用组件
│   │   │   ├── map/              # 地图组件
│   │   │   └── ai/               # AI组件
│   │   ├── views/                 # 页面
│   │   │   ├── home/             # 首页
│   │   │   ├── trip/             # 行程页面
│   │   │   ├── profile/          # 个人中心
│   │   │   └── auth/             # 认证页面
│   │   ├── router/                # 路由配置
│   │   ├── stores/                # 状态管理
│   │   ├── services/              # 服务层
│   │   │   └── api/              # API服务
│   │   ├── types/                 # TypeScript类型
│   │   ├── utils/                 # 工具函数
│   │   ├── App.vue                # 应用根组件
│   │   └── main.ts                # 入口文件
│   ├── public/                    # 公共文件
│   ├── index.html                 # HTML模板
│   ├── package.json               # 项目配置
│   ├── tsconfig.json              # TypeScript配置
│   └── vite.config.ts             # Vite配置
│
├── .env.example                   # 环境变量示例
├── README.md                      # 项目文档
└── LICENSE                        # 许可证
```

## 🚀 快速开始

### 前置要求

- Node.js >= 18.0.0
- Java >= 17
- MySQL >= 8.0
- Redis >= 7.0
- Pinecone账号
- OpenAI API Key

### 本地开发

#### 1. 克隆项目

```bash
git clone <repository-url>
cd travel-agent
```

#### 2. 配置环境变量

```bash
cp .env.example .env
# 编辑.env文件，配置必要的环境变量
```

#### 3. 启动后端

```bash
cd travel-agent-backend

# 初始化数据库
mysql -u root -p < src/main/resources/db/schema.sql

# 启动应用
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

#### 4. 启动前端

```bash
cd travel-agent-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端应用将在 http://localhost:3000 启动

## ⚙️ 配置说明

### 后端配置

#### application-dev.yml (开发环境)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/travel_agent_dev
    username: root
    password: root

redis:
  host: localhost
  port: 6379

pinecone:
  api:
    key: your-dev-api-key
  environment: us-west1-gcp

openai:
  api:
    key: your-dev-api-key
```

#### application-prod.yml (生产环境)

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}

redis:
  host: ${REDIS_HOST}
  port: ${REDIS_PORT}

pinecone:
  api:
    key: ${PINECONE_API_KEY}
  environment: ${PINECONE_ENVIRONMENT}

openai:
  api:
    key: ${OPENAI_API_KEY}
```

### 前端配置

#### .env.development

```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

#### .env.production

```bash
VITE_API_BASE_URL=https://api.yourdomain.com/api
```

## 🛠️ 开发指南

### 后端开发

#### 添加新API

1. 创建Controller
2. 创建Service
3. 创建Repository
4. 创建DTO

#### 添加新AI功能

1. 在 `service/ai/` 下创建服务类
2. 在 `config/AIConfig.java` 中配置Bean
3. 实现具体功能逻辑

### 前端开发

#### 添加新页面

1. 在 `src/views/` 下创建页面组件
2. 在 `src/router/index.ts` 中添加路由配置
3. 在 `src/services/api/` 下创建API服务

#### 添加新组件

1. 在 `src/components/` 下创建组件
2. 在需要的页面中引入使用

## 📋 技术栈详细说明

### 后端技术栈

- **Spring Boot 3.2.0** - Java后端框架
- **LangChain4j 0.32.0** - AI集成框架
- **MySQL 8.0** - 关系型数据库
- **Pinecone** - 向量数据库（长期记忆）
- **Redis Cloud** - 缓存服务（短期记忆）
- **OpenAI API** - 大语言模型
- **Maven** - 项目构建工具

### 前端技术栈

- **Vue 3.4** - 响应式前端框架
- **TypeScript** - 类型安全
- **Pinia** - 状态管理
- **Vue Router 4** - 路由管理
- **Element Plus** - UI组件库
- **Axios** - HTTP客户端
- **Vite** - 构建工具
- **Tailwind CSS** - CSS框架

## 🤝 贡献指南

1. Fork项目
2. 创建分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交Pull Request

## 📄 许可证

MIT License

## 📞 联系方式

- 项目主页: <https://github.com/your-username/travel-agent>
- 问题反馈: <https://github.com/your-username/travel-agent/issues>

## 🙏 致谢

- LangChain4j团队
- Vue团队
- Element Plus团队
- 所有贡献者
