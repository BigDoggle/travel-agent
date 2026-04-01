package com.travel.agent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AI配置类
 */
@Slf4j
@Configuration
@Profile({"dev", "prod"})
public class AIConfig {
    
    public AIConfig() {
        log.info("AIConfig initialized");
    }
}
