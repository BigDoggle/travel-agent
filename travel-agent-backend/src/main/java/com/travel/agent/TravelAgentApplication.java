package com.travel.agent;

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
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class TravelAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelAgentApplication.class, args);
        System.out.println("========================================");
        System.out.println("Travel Agent Backend Server Started!");
        System.out.println("========================================");
    }
}
