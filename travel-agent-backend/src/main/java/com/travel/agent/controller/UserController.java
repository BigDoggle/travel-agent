package com.travel.agent.controller;

import com.travel.agent.entity.User;
import com.travel.agent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户控制器
 */
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
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid LoginRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userService.findByUsername(request.getUsername());
        
        if (userOpt.isPresent() && userService.validateUser(request.getUsername(), request.getPassword())) {
            User user = userOpt.get();
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", user);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            
            User savedUser = userService.registerUser(user);
            
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("data", savedUser);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userService.findById(id);
        
        if (userOpt.isPresent()) {
            response.put("success", true);
            response.put("data", userOpt.get());
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
        }
        
        return ResponseEntity.ok(response);
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