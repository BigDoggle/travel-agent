package com.travel.agent.service.ai;

import com.travel.agent.entity.ChatRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 快速模式集成测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FastModeIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AIChatService aiChatService;

    @Test
    public void testFastModeService() {
        // 测试快速模式服务层
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "推荐一个适合周末的旅行目的地";

        Flux<String> response = aiChatService.processMessageStream(userId, sessionId, message, true);

        assertNotNull(response, "快速模式响应不应为空");

        // 验证响应包含内容
        StringBuilder fullResponse = new StringBuilder();
        response.subscribe(
            token -> fullResponse.append(token),
            error -> fail("快速模式处理出错: " + error.getMessage()),
            () -> {
                assertFalse(fullResponse.toString().isEmpty(), "快速模式响应不应为空");
                assertTrue(fullResponse.toString().length() > 10, "快速模式响应长度应大于10个字符");
            }
        );
    }

    @Test
    public void testStandardModeService() {
        // 测试标准模式服务层
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "推荐一个适合周末的旅行目的地";

        Flux<String> response = aiChatService.processMessageStream(userId, sessionId, message, false);

        assertNotNull(response, "标准模式响应不应为空");

        // 验证响应包含内容
        StringBuilder fullResponse = new StringBuilder();
        response.subscribe(
            token -> fullResponse.append(token),
            error -> fail("标准模式处理出错: " + error.getMessage()),
            () -> {
                assertFalse(fullResponse.toString().isEmpty(), "标准模式响应不应为空");
                assertTrue(fullResponse.toString().length() > 10, "标准模式响应长度应大于10个字符");
            }
        );
    }

    @Test
    public void testBackwardCompatibility() {
        // 测试向后兼容性
        Long userId = 1L;
        String sessionId = "test-session";
        String message = "推荐一个适合周末的旅行目的地";

        Flux<String> response = aiChatService.processMessageStream(userId, sessionId, message);

        assertNotNull(response, "向后兼容模式响应不应为空");

        // 验证响应包含内容
        StringBuilder fullResponse = new StringBuilder();
        response.subscribe(
            token -> fullResponse.append(token),
            error -> fail("向后兼容模式处理出错: " + error.getMessage()),
            () -> {
                assertFalse(fullResponse.toString().isEmpty(), "向后兼容模式响应不应为空");
                assertTrue(fullResponse.toString().length() > 10, "向后兼容模式响应长度应大于10个字符");
            }
        );
    }

    @Test
    public void testControllerFastMode() {
        // 测试控制器层快速模式
        String url = "http://localhost:" + port + "/api/ai/chat/stream";

        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setSessionId("test-session");
        request.setMessage("推荐一个适合周末的旅行目的地");
        request.setFastMode(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "快速模式API应返回200状态码");
    }

    @Test
    public void testControllerStandardMode() {
        // 测试控制器层标准模式
        String url = "http://localhost:" + port + "/api/ai/chat/stream";

        ChatRequest request = new ChatRequest();
        request.setUserId(1L);
        request.setSessionId("test-session");
        request.setMessage("推荐一个适合周末的旅行目的地");
        request.setFastMode(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "标准模式API应返回200状态码");
    }
}