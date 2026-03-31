<template>
  <nav class="navbar">
    <h1>AI智能旅行规划系统</h1>
  </nav>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 导航栏组件
 */
defineComponent({
  name: 'Navbar'
})
</script>

<style scoped>
.navbar {
  background-color: #409EFF;
  padding: 1rem 2rem;
  color: white;
}

h1 {
  font-size: 1.5rem;
  margin: 0;
}
</style>
