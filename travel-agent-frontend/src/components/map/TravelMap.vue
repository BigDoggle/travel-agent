<template>
  <div class="travel-map">
    <h1>地图导航</h1>
    <p>地图组件</p>
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 地图组件
 */
defineComponent({
  name: 'TravelMap'
})
</script>

<style scoped>
.travel-map {
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
