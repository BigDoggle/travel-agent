<template>
  <div class="trip-detail-view">
    <h1>行程详情</h1>
    <p>行程详情视图</p>
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 行程详情视图
 */
defineComponent({
  name: 'TripDetailView'
})
</script>

<style scoped>
.trip-detail-view {
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
