package com.travel.agent.controller;

import com.travel.agent.common.Result;
import com.travel.agent.entity.User;
import com.travel.agent.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody @Valid LoginRequest request) {
        log.info("用户登录请求，用户名: {}", request.getUsername());
        User user = userService.findByUsername(request.getUsername());
        
        if (user != null && userService.validateUser(request.getUsername(), request.getPassword())) {
            log.info("用户登录成功，用户名: {}", request.getUsername());
            return Result.success("登录成功", user);
        }
        
        log.warn("用户登录失败，用户名: {}", request.getUsername());
        return Result.error("用户名或密码错误");
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody @Valid RegisterRequest request) {
        log.info("用户注册请求，用户名: {}, 邮箱: {}", request.getUsername(), request.getEmail());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        
        User savedUser = userService.registerUser(user);
        
        log.info("用户注册成功，用户名: {}", request.getUsername());
        return Result.success("注册成功", savedUser);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        log.info("获取用户信息请求，用户ID: {}", id);
        User user = userService.findById(id);
        
        if (user != null) {
            log.info("获取用户信息成功，用户ID: {}", id);
            return Result.success("获取成功", user);
        }
        
        log.warn("获取用户信息失败，用户ID: {}", id);
        return Result.error("用户不存在");
    }

    /**
     * 登录请求体
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /**
     * 注册请求体
     */
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
