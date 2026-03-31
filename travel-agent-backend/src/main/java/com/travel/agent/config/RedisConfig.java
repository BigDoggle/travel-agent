package com.travel.agent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Redis配置类
 */
@Configuration
@Profile({"dev", "prod"})
public class RedisConfig {
}
