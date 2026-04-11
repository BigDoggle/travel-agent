import java.io.*;
import java.net.*;

/**
 * 快速模式功能验证脚本
 */
public class test-fast-mode {
    public static void main(String[] args) {
        try {
            // 测试快速模式 API
            testFastModeAPI();
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testFastModeAPI() throws Exception {
        URL url = new URL("http://localhost:8080/api/ai/chat/stream");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // 构造请求体
        String requestBody = "{\n" +
                "  \"userId\": 1,\n" +
                "  \"sessionId\": \"test-session\",\n" +
                "  \"message\": \"推荐一个适合周末的旅行目的地\",\n" +
                "  \"fastMode\": true\n" +
                "}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        System.out.println("响应码: " + responseCode);

        if (responseCode == 200) {
            System.out.println("快速模式 API 测试成功！");
        } else {
            System.out.println("快速模式 API 测试失败，响应码: " + responseCode);
        }
    }
}