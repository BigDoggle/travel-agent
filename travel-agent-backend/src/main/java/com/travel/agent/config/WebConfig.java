package com.travel.agent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置异步请求支持
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置异步请求支持
     * 增加超时时间以支持长时间的 AI 处理任务
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 设置异步请求超时时间为 5 分钟（300000 毫秒）
        // 因为多智能体协调可能需要较长时间（规划、执行、报告生成）
        long timeout = 300000L; // 5分钟
        configurer.setDefaultTimeout(timeout);
        
        log.info("异步请求超时时间已设置为: {} ms ({} 秒)", timeout, timeout / 1000);
    }
}
