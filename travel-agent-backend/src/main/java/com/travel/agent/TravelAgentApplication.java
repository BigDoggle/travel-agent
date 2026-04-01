package com.travel.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AI智能旅行规划系统主应用
 * 
 * @author Travel Agent Team
 * @version 1.0.0
 * @since 2024-03-30
 */
@Slf4j
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class TravelAgentApplication {

    public static void main(String[] args) {
        log.info("Starting Travel Agent Backend Server...");
        SpringApplication.run(TravelAgentApplication.class, args);
        log.info("========================================");
        log.info("Travel Agent Backend Server Started!");
        log.info("========================================");
    }
}
