package com.travel.agent.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String username;

    private String email;

    private String password;

    private String avatar;

    private String phone;

    private Integer status = 1;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
