<template>
  <div class="trip-view">
    <h1>行程管理</h1>
    <p>行程视图</p>
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 行程视图
 */
defineComponent({
  name: 'TripView'
})
</script>

<style scoped>
.trip-view {
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
