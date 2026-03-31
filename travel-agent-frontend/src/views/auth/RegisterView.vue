<template>
  <div class="register-view">
    <h1>用户注册</h1>
    <p>注册视图</p>
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 注册视图
 */
defineComponent({
  name: 'RegisterView'
})
</script>

<style scoped>
.register-view {
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
