<template>
  <div class="login-container">
    <div class="login-form">
      <h2>用户登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label for="username">用户名</label>
          <input 
            id="username" 
            v-model="loginForm.username" 
            type="text" 
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="input-group">
          <label for="password">密码</label>
          <input 
            id="password" 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码"
            required
          />
        </div>
        <button type="submit" class="submit-btn">登录</button>
      </form>
      <div class="switch-form">
        <p>还没有账户？<span @click="switchToRegister" class="link">立即注册</span></p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

interface LoginForm {
  username: string
  password: string
}

const router = useRouter()
const loginForm = ref<LoginForm>({
  username: '',
  password: ''
})

const handleLogin = () => {
  // 这里是登录逻辑，实际项目中应调用API
  if (loginForm.value.username && loginForm.value.password) {
    // 模拟登录成功
    localStorage.setItem('user', JSON.stringify({ username: loginForm.value.username }))
    ElMessage.success('登录成功')
    router.push('/home')
  } else {
    ElMessage.error('请输入用户名和密码')
  }
}

const switchToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.3) 0%, rgba(255, 255, 224, 0.3) 100%);
  backdrop-filter: blur(10px);
  padding: 20px;
}

.login-form {
  background: rgba(255, 255, 240, 0.85);
  border-radius: 12px;
  padding: 30px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 8px 32px rgba(139, 69, 19, 0.1);
  border: 1px solid rgba(255, 228, 196, 0.3);
}

h2 {
  text-align: center;
  margin-bottom: 24px;
  color: #8b5a2b;
  font-weight: 500;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  margin-bottom: 6px;
  color: #8b5a2b;
  font-size: 14px;
}

.input-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid rgba(210, 180, 140, 0.5);
  border-radius: 6px;
  background: rgba(255, 255, 245, 0.8);
  color: #333;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.input-group input:focus {
  outline: none;
  border-color: #d2b48c;
  box-shadow: 0 0 0 2px rgba(210, 180, 140, 0.2);
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(to right, #d2b48c, #e6bc6d);
  border: none;
  border-radius: 6px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.submit-btn:hover {
  opacity: 0.9;
}

.switch-form {
  text-align: center;
  margin-top: 20px;
}

.switch-form p {
  color: #8b5a2b;
  font-size: 14px;
}

.link {
  color: #d2b48c;
  cursor: pointer;
  text-decoration: underline;
}

.link:hover {
  color: #c19e6b;
}
</style>
