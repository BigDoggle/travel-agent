package com.travel.agent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBase {

    private Long id;

    private String knowledgeType;

    private String location;

    private String locationId;

    private String content;

    private String category;

    private String tags;

    private String source;

    private Double rating = 0.00;

    private Integer popularity = 0;

    private Integer version = 1;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
