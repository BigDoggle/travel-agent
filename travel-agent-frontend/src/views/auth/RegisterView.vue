<template>
  <div class="register-container">
    <div class="register-form">
      <h2>用户注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="input-group">
          <label for="username">用户名</label>
          <input 
            id="username" 
            v-model="registerForm.username" 
            type="text" 
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="input-group">
          <label for="password">密码</label>
          <input 
            id="password" 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码"
            required
          />
        </div>
        <div class="input-group">
          <label for="confirmPassword">确认密码</label>
          <input 
            id="confirmPassword" 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            required
          />
        </div>
        <button type="submit" class="submit-btn">注册</button>
      </form>
      <div class="switch-form">
        <p>已有账户？<span @click="switchToLogin" class="link">立即登录</span></p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
}

const router = useRouter()
const registerForm = ref<RegisterForm>({
  username: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = () => {
  if (!registerForm.value.username || !registerForm.value.password) {
    ElMessage.error('请输入用户名和密码')
    return
  }

  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }

  if (registerForm.value.password.length < 6) {
    ElMessage.error('密码长度至少为6位')
    return
  }

  // 这里是注册逻辑，实际项目中应调用API
  // 模拟注册成功
  localStorage.setItem('user', JSON.stringify({ username: registerForm.value.username }))
  ElMessage.success('注册成功')
  router.push('/login')
}

const switchToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.3) 0%, rgba(255, 255, 224, 0.3) 100%);
  backdrop-filter: blur(10px);
  padding: 20px;
}

.register-form {
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
