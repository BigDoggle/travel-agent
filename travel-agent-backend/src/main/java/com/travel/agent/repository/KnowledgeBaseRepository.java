package com.travel.agent.repository;

import com.travel.agent.entity.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 知识库仓储接口
 */
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {

    List<KnowledgeBase> findByKnowledgeTypeAndLocation(String knowledgeType, String location);

    List<KnowledgeBase> findByCategory(String category);

    List<KnowledgeBase> findByKnowledgeType(String knowledgeType);
}
