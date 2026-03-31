<template>
  <footer class="footer">
    <p>© 2024 AI智能旅行规划系统</p>
  </footer>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 底部组件
 */
defineComponent({
  name: 'Footer'
})
</script>

<style scoped>
.footer {
  background-color: #f5f7fa;
  padding: 1rem;
  text-align: center;
  border-top: 1px solid #dcdfe6;
}

p {
  margin: 0;
  color: #909399;
}
</style>
