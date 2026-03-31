<template>
  <div class="base-component">
    <slot />
  </div>
</template>

<script setup lang="ts">
import { defineComponent } from 'vue'

/**
 * 通用组件基类
 */
defineComponent({
  name: 'BaseComponent'
})
</script>

<style scoped>
.base-component {
  width: 100%;
  height: 100%;
}
</style>
