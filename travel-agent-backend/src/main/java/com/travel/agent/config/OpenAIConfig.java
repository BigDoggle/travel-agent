package com.travel.agent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * OpenAI配置类
 */
@Slf4j
@Configuration
@Profile({"dev", "prod"})
public class OpenAIConfig {
    
    public OpenAIConfig() {
        log.info("OpenAIConfig initialized");
    }
}
