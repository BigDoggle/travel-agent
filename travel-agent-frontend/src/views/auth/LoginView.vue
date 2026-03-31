<template>
  <div class="login-view">
    <h1>用户登录</h1>
    <p>登录视图</p>
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 登录视图
 */
defineComponent({
  name: 'LoginView'
})
</script>

<style scoped>
.login-view {
  padding: 2rem;
  text-align: center;
}

h1 {
  font-size: 2.5rem;
  color: #303133;
  margin-bottom: 1rem;
}

p {
  font-size: 1.1rem;
  color: #606266;
}
</style>
