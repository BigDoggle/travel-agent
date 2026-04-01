package com.travel.agent.service;

import com.travel.agent.common.BusinessException;
import com.travel.agent.entity.User;
import com.travel.agent.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务类
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    /**
     * 根据邮箱查找用户
     */
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    /**
     * 注册新用户
     */
    public User registerUser(User user) {
        log.info("开始注册新用户，用户名: {}, 邮箱: {}", user.getUsername(), user.getEmail());
        
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            log.warn("用户名已存在，用户名: {}", user.getUsername());
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        User existingEmail = userMapper.findByEmail(user.getEmail());
        if (existingEmail != null) {
            log.warn("邮箱已被注册，邮箱: {}", user.getEmail());
            throw new BusinessException("邮箱已被注册");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认状态
        user.setStatus(1);

        userMapper.insert(user);
        log.info("用户注册成功，用户名: {}", user.getUsername());
        return user;
    }

    /**
     * 更新用户信息
     */
    public User updateUser(Long id, User userDetails) {
        log.info("开始更新用户信息，用户ID: {}", id);
        User user = userMapper.findById(id);
        if (user == null) {
            log.warn("用户不存在，用户ID: {}", id);
            throw new BusinessException("用户不存在");
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        user.setAvatar(userDetails.getAvatar());
        user.setPhone(userDetails.getPhone());

        userMapper.update(user);
        log.info("用户信息更新成功，用户ID: {}", id);
        return user;
    }

    /**
     * 验证用户凭据
     */
    public boolean validateUser(String username, String password) {
        log.debug("验证用户凭据，用户名: {}", username);
        User user = userMapper.findByUsername(username);
        if (user != null) {
            boolean isValid = passwordEncoder.matches(password, user.getPassword());
            if (isValid) {
                log.debug("用户凭据验证成功，用户名: {}", username);
            } else {
                log.debug("用户凭据验证失败，用户名: {}", username);
            }
            return isValid;
        }
        log.debug("用户不存在，用户名: {}", username);
        return false;
    }

    /**
     * 根据ID查找用户
     */
    public User findById(Long id) {
        return userMapper.findById(id);
    }
}
