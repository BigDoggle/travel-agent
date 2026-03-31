# AI智能旅行规划系统 - 后端开发指南

## 项目结构

```
travel-agent-backend/
├── src/
│   ├── main/
│   │   ├── java/com/travel/agent/
│   │   │   ├── config/           # 配置类
│   │   │   ├── controller/       # 控制器
│   │   │   ├── service/          # 服务层
│   │   │   │   ├── ai/          # AI服务
│   │   │   │   └── agent/       # Agent服务
│   │   │   ├── repository/       # 数据访问
│   │   │   ├── entity/           # 实体
│   │   │   ├── dto/              # 数据传输对象
│   │   │   └── exception/        # 异常处理
│   │   └── resources/
│   │       ├── application.yml      # 主配置
│   │       ├── application-dev.yml  # 开发配置
│   │       ├── application-prod.yml # 生产配置
│   │       └── db/
│   │           └── schema.sql       # 数据库初始化
│   └── test/
│       └── java/com/travel/agent/
├── pom.xml
└── target
```

## 开发指南

### 1. 添加新API

#### 1.1 创建DTO

```java
// src/main/java/com/travel/agent/dto/RequestDTO.java
@Data
public class RequestDTO {
    private String field1;
    private String field2;
}
```

#### 1.2 创建Controller

```java
// src/main/java/com/travel/agent/controller/NewController.java
@RestController
@RequestMapping("/api/news")
public class NewController extends BaseController {
    
    @Autowired
    private NewService newService;
    
    @PostMapping
    public ResponseDTO create(@RequestBody RequestDTO request) {
        return ResponseDTO.success(newService.create(request));
    }
    
    @GetMapping("/{id}")
    public ResponseDTO get(@PathVariable Long id) {
        return ResponseDTO.success(newService.get(id));
    }
}
```

#### 1.3 创建Service

```java
// src/main/java/com/travel/agent/service/NewService.java
@Service
public class NewService {
    
    @Autowired
    private NewRepository repository;
    
    public NewEntity create(RequestDTO request) {
        // 实现业务逻辑
    }
    
    public NewEntity get(Long id) {
        return repository.findById(id).orElse(null);
    }
}
```

#### 1.4 创建Repository

```java
// src/main/java/com/travel/agent/repository/NewRepository.java
public interface NewRepository extends JpaRepository<NewEntity, Long> {
    // 添加自定义查询方法
}
```

### 2. 添加AI功能

#### 2.1 创建AI服务接口

```java
// src/main/java/com/travel/agent/service/ai/NewAIService.java
public interface NewAIService extends AIService {
    Result process(Request request);
}
```

#### 2.2 实现AI服务

```java
// src/main/java/com/travel/agent/service/ai/impl/NewAIServiceImpl.java
@Service
public class NewAIServiceImpl implements NewAIService {
    
    @Autowired
    private ChatLanguageModel chatLanguageModel;
    
    @Override
    public Result process(Request request) {
        // 实现AI逻辑
    }
}
```

#### 2.3 配置AI Bean

```java
// src/main/java/com/travel/agent/config/AIConfig.java
@Configuration
public class AIConfig {
    
    @Bean
    public NewAIService newAIService() {
        return new NewAIServiceImpl();
    }
}
```

### 3. 添加Agent

#### 3.1 创建Agent接口

```java
// src/main/java/com/travel/agent/service/ai/agent/NewAgent.java
public interface NewAgent extends AgentService {
    Result execute(Request request);
}
```

#### 3.2 实现Agent

```java
// src/main/java/com/travel/agent/service/ai/agent/impl/NewAgentImpl.java
@Service
public class NewAgentImpl implements NewAgent {
    
    @Autowired
    private NewAIService aiService;
    
    @Override
    public Result execute(Request request) {
        // 实现Agent逻辑
    }
}
```

### 4. 添加Tool

#### 4.1 创建Tool接口

```java
// src/main/java/com/travel/agent/service/ai/tool/NewTool.java
public interface NewTool extends ToolService {
    Result execute(Input input);
}
```

#### 4.2 实现Tool

```java
// src/main/java/com/travel/agent/service/ai/tool/impl/NewToolImpl.java
@Service
public class NewToolImpl implements NewTool {
    
    @Override
    public Result execute(Input input) {
        // 实现Tool逻辑
    }
}
```

## 测试指南

### 单元测试

```java
@SpringBootTest
class NewServiceTest {
    
    @Autowired
    private NewService service;
    
    @Test
    void testCreate() {
        // 测试代码
    }
}
```

### 集成测试

```java
@SpringBootTest
class NewControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testCreate() throws Exception {
        // 测试代码
    }
}
```

## 部署指南

### 本地部署

```bash
cd travel-agent-backend
mvn clean package
java -jar target/travel-agent-backend-1.0.0-SNAPSHOT.jar
```

## 常见问题

### 1. 端口冲突

修改 `application.yml` 中的 `server.port`

### 2. 数据库连接失败

检查数据库配置和网络连接

### 3. API密钥配置

确保 `.env` 文件中的API密钥正确配置

## 贡献指南

1. Fork项目
2. 创建分支
3. 提交更改
4. 推送到分支
5. 提交Pull Request

## 许可证

MIT License
