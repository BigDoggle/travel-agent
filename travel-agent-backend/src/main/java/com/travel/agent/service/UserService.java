package com.travel.agent.service;

import com.travel.agent.common.BusinessException;
import com.travel.agent.entity.User;
import com.travel.agent.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务类
 */
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
        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        User existingEmail = userMapper.findByEmail(user.getEmail());
        if (existingEmail != null) {
            throw new BusinessException("邮箱已被注册");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 设置默认状态
        user.setStatus(1);

        userMapper.insert(user);
        return user;
    }

    /**
     * 更新用户信息
     */
    public User updateUser(Long id, User userDetails) {
        User user = userMapper.findById(id);
        if (user == null) {
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
        return user;
    }

    /**
     * 验证用户凭据
     */
    public boolean validateUser(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    /**
     * 根据ID查找用户
     */
    public User findById(Long id) {
        return userMapper.findById(id);
    }
}
