<template>
  <div class="ai-chat">
    <h1>AI对话</h1>
    <p>AI对话组件</p>
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * AI对话组件
 */
defineComponent({
  name: 'AIChat'
})
</script>

<style scoped>
.ai-chat {
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
