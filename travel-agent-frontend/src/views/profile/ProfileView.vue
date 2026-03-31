<template>
  <div class="profile-view">
    <h1>个人中心</h1>
    <p>个人中心视图</p>
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 个人中心视图
 */
defineComponent({
  name: 'ProfileView'
})
</script>

<style scoped>
.profile-view {
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
