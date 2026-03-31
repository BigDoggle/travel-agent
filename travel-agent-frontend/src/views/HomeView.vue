<template>
  <div class="home-view">
    <h1>AI智能旅行规划系统</h1>
    <p>欢迎使用AI智能旅行规划系统</p>
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 首页视图
 */
defineComponent({
  name: 'HomeView'
})
</script>

<style scoped>
.home-view {
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
