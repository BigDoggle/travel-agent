# 配置说明

## 环境配置

本项目使用 Spring Boot 的配置系统，支持多种配置方式。

### 方式一：使用示例配置文件（推荐新手）

1. 复制 `application-example.yml` 为 `application.yml`：

```bash
# Windows PowerShell
Copy-Item travel-agent-backend/src/main/resources/application-example.yml travel-agent-backend/src/main/resources/application.yml

# Linux/Mac
cp travel-agent-backend/src/main/resources/application-example.yml travel-agent-backend/src/main/resources/application.yml
```

2. 编辑 `application.yml`，填写实际的数据库连接信息和 API Key

### 方式二：使用环境变量（推荐生产环境）

设置以下环境变量来覆盖配置：

#### 数据库配置
- `DATABASE_URL` - 数据库连接 URL
- `DATABASE_USERNAME` - 数据库用户名
- `DATABASE_PASSWORD` - 数据库密码

#### Redis 配置
- `REDIS_HOST` - Redis 主机地址
- `REDIS_PORT` - Redis 端口
- `REDIS_PASSWORD` - Redis 密码

#### Pinecone 配置
- `PINECONE_API_KEY` - Pinecone API Key
- `PINECONE_ENVIRONMENT` - Pinecone 环境

#### OpenAI 配置
- `OPENAI_API_KEY` - OpenAI API Key

##### Windows PowerShell 示例
```powershell
$env:DATABASE_URL="jdbc:postgresql://db.oxxzzzzynkirfmpcobhh.supabase.co:5432/postgres?user=postgres&password=your-password"
$env:DATABASE_USERNAME="postgres"
$env:DATABASE_PASSWORD="your-password"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:PINECONE_API_KEY="your-pinecone-api-key"
$env:OPENAI_API_KEY="your-openai-api-key"
```

##### Linux/Mac 示例
```bash
export DATABASE_URL="jdbc:postgresql://db.oxxzzzzynkirfmpcobhh.supabase.co:5432/postgres?user=postgres&password=your-password"
export DATABASE_USERNAME="postgres"
export DATABASE_PASSWORD="your-password"
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export PINECONE_API_KEY="your-pinecone-api-key"
export OPENAI_API_KEY="your-openai-api-key"
```

## 必需配置

### 1. Supabase 数据库

注册地址：https://supabase.com/

```yaml
spring:
  datasource:
    url: jdbc:postgresql://db.xxx.supabase.co:5432/postgres?user=postgres&password=your-password
    username: postgres
    password: your-password
    driver-class-name: org.postgresql.Driver
```

### 2. Pinecone 向量数据库（可选）

注册地址：https://www.pinecone.io/

```yaml
pinecone:
  api:
    key: your-pinecone-api-key
  environment: us-west1-gcp
  index:
    preferences: travel-preferences
    history: travel-history
    knowledge: knowledge-base
```

### 3. OpenAI API（可选）

注册地址：https://platform.openai.com/

```yaml
openai:
  api:
    key: your-openai-api-key
  model: gpt-4
  embedding:
    model: text-embedding-ada-002
```

### 4. Redis（可选）

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
```

## 配置优先级

环境变量 > application.yml > application-example.yml

## 安全建议

1. **不要将包含真实密码的 application.yml 提交到 Git**
   - 项目已配置 `.gitignore` 自动忽略 `application.yml`
   
2. **使用环境变量管理敏感信息**
   - 在服务器或 CI/CD 环境中设置环境变量
   
3. **示例配置文件提供占位符**
   - `application-example.yml` 仅包含示例值
   
4. **生产环境使用独立的 API Key**
   - 不要在多个项目间共享 API Key

## 故障排查

### 数据库连接失败
- 检查数据库 URL、用户名、密码是否正确
- 确认数据库服务正在运行
- 检查网络连接和防火墙设置

### API Key 无效
- 确认 API Key 已激活
- 检查 API Key 是否有权限
- 确认余额充足

### Redis 连接失败
- 确认 Redis 服务正在运行
- 检查 Redis 主机和端口配置
- 如果不需要 Redis，可以跳过相关配置
