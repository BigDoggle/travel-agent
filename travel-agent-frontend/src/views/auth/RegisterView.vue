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
          <label for="email">邮箱</label>
          <input 
            id="email" 
            v-model="registerForm.email" 
            type="email" 
            placeholder="请输入邮箱"
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
        <button type="submit" class="submit-btn" :disabled="loading">注册</button>
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
import { userApi } from '@/services/api/userApi'

interface RegisterForm {
  username: string
  email: string
  password: string
  confirmPassword: string
}

const router = useRouter()
const loading = ref(false)
const registerForm = ref<RegisterForm>({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  if (!registerForm.value.username || !registerForm.value.email || !registerForm.value.password) {
    ElMessage.error('请输入用户名、邮箱和密码')
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

  loading.value = true
  try {
    const response = await userApi.register({
      username: registerForm.value.username,
      email: registerForm.value.email,
      password: registerForm.value.password
    })
    
    if (response.success) {
      ElMessage.success(response.message)
      router.push('/login')
    } else {
      ElMessage.error(response.message || '注册失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '注册失败，请检查网络连接')
  } finally {
    loading.value = false
  }
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
  background: white;
  padding: 20px;
}

.register-form {
  background: white;
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
  color: black;
  font-weight: 500;
}

.input-group {
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  margin-bottom: 6px;
  color: black;
  font-size: 14px;
}

.input-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid rgba(255, 228, 196, 0.5);
  border-radius: 6px;
  background: white;
  color: #333;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.input-group input:focus {
  outline: none;
  border-color: #d2b48c;
  box-shadow: 0 0 0 2px rgba(255, 248, 220, 0.5);
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(to right, #f5e6c8, #f8f0d8);
  border: 1px solid rgba(255, 228, 196, 0.5);
  border-radius: 6px;
  color: black;
  font-size: 16px;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.submit-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.switch-form {
  text-align: center;
  margin-top: 20px;
}

.switch-form p {
  color: black;
  font-size: 14px;
}

.link {
  color: #f5e6c8;
  cursor: pointer;
  text-decoration: underline;
}

.link:hover {
  color: #f8f0d8;
}
</style>
