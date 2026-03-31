package com.travel.agent.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库实体
 */
@Entity
@Table(name = "knowledge_base", indexes = {
    @Index(name = "idx_type_location", columnList = "knowledge_type, location"),
    @Index(name = "idx_category", columnList = "category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "knowledge_type", length = 50)
    private String knowledgeType;

    @Column(length = 100)
    private String location;

    @Column(name = "location_id", length = 100)
    private String locationId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 50)
    private String category;

    @Column(columnDefinition = "JSON")
    private String tags;

    @Column(length = 20)
    private String source;

    @Column(precision = 2, scale = 1)
    private Double rating = 0.00;

    @Column
    private Integer popularity = 0;

    @Column
    private Integer version = 1;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
