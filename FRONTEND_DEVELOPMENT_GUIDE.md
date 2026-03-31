# AI智能旅行规划系统 - 前端开发指南

## 项目结构

```
travel-agent-frontend/
├── src/
│   ├── assets/               # 静态资源
│   ├── components/           # 组件
│   │   ├── common/          # 通用组件
│   │   ├── map/             # 地图组件
│   │   └── ai/              # AI组件
│   ├── views/               # 页面
│   │   ├── home/            # 首页
│   │   ├── trip/            # 行程页面
│   │   ├── profile/         # 个人中心
│   │   └── auth/            # 认证页面
│   ├── router/              # 路由配置
│   ├── stores/              # 状态管理
│   ├── services/            # 服务层
│   │   └── api/            # API服务
│   ├── types/               # TypeScript类型
│   ├── utils/               # 工具函数
│   ├── App.vue              # 应用根组件
│   └── main.ts              # 入口文件
├── public/                  # 公共文件
├── index.html               # HTML模板
├── package.json             # 项目配置
├── tsconfig.json            # TypeScript配置
└── vite.config.ts           # Vite配置
```

## 开发指南

### 1. 添加新页面

#### 1.1 创建页面组件

```vue
<!-- src/views/NewView.vue -->
<template>
  <div class="new-view">
    <h1>新页面</h1>
  </div>
</template>

<script setup lang="ts">
// 组件逻辑
</script>

<style scoped>
.new-view {
  padding: 20px;
}
</style>
```

#### 1.2 添加路由配置

```typescript
// src/router/index.ts
const routes = [
  // ... 其他路由
  {
    path: '/new',
    name: 'New',
    component: () => import('@/views/NewView.vue'),
    meta: { title: '新页面' }
  }
]
```

### 2. 添加新组件

#### 2.1 创建组件

```vue
<!-- src/components/new/NewComponent.vue -->
<template>
  <div class="new-component">
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
// 组件逻辑
</script>

<style scoped>
.new-component {
  /* 样式 */
}
</style>
```

#### 2.2 注册组件

```vue
<!-- 在其他组件中使用 -->
<script setup lang="ts">
import NewComponent from '@/components/new/NewComponent.vue'
</script>

<template>
  <NewComponent>
    内容
  </NewComponent>
</template>
```

### 3. 添加API服务

#### 3.1 创建API服务

```typescript
// src/services/api/newApi.ts
import api from '../api'

export const newApi = {
  /**
   * 获取列表
   */
  list: (params?: any) => {
    return api.get('/news', { params })
  },

  /**
   * 获取详情
   */
  get: (id: string) => {
    return api.get(`/news/${id}`)
  },

  /**
   * 创建
   */
  create: (data: any) => {
    return api.post('/news', data)
  },

  /**
   * 更新
   */
  update: (id: string, data: any) => {
    return api.put(`/news/${id}`, data)
  },

  /**
   * 删除
   */
  delete: (id: string) => {
    return api.delete(`/news/${id}`)
  }
}
```

#### 3.2 在组件中使用

```vue
<script setup lang="ts">
import { ref } from 'vue'
import { newApi } from '@/services/api/newApi'

const news = ref([])

const loadNews = async () => {
  try {
    const response = await newApi.list()
    news.value = response.data
  } catch (error) {
    console.error('获取新闻失败', error)
  }
}
</script>
```

### 4. 添加状态管理

#### 4.1 创建Store

```typescript
// src/stores/useNewStore.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNewStore = defineStore('new', () => {
  const news = ref([])
  const loading = ref(false)

  const loadNews = async () => {
    loading.value = true
    try {
      // 加载逻辑
    } finally {
      loading.value = false
    }
  }

  return {
    news,
    loading,
    loadNews
  }
})
```

#### 4.2 在组件中使用

```vue
<script setup lang="ts">
import { useNewStore } from '@/stores/useNewStore'

const newStore = useNewStore()
</script>
```

### 5. 添加类型定义

```typescript
// src/types/new.ts
export interface New {
  id: number
  title: string
  content: string
  createdAt: string
  updatedAt: string
}
```

## 常见模式

### 1. 表单处理

```vue
<template>
  <el-form :model="form" @submit.prevent="handleSubmit">
    <el-form-item label="名称" prop="name">
      <el-input v-model="form.name" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" native-type="submit">提交</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const form = ref({
  name: '',
  description: ''
})

const handleSubmit = async () => {
  try {
    // 提交逻辑
  } catch (error) {
    console.error('提交失败', error)
  }
}
</script>
```

### 2. 列表渲染

```vue
<template>
  <div class="list">
    <el-card v-for="item in list" :key="item.id">
      <h3>{{ item.title }}</h3>
      <p>{{ item.description }}</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const list = ref([])

onMounted(async () => {
  // 加载列表
})
</script>
```

### 3. 表格展示

```vue
<template>
  <el-table :data="tableData" style="width: 100%">
    <el-table-column prop="name" label="名称" />
    <el-table-column prop="description" label="描述" />
    <el-table-column label="操作">
      <template #default="scope">
        <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
        <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>
```

## 性能优化

### 1. 路由懒加载

```typescript
// 已配置
component: () => import('@/views/NewView.vue')
```

### 2. 组件懒加载

```vue
<template>
  <Suspense>
    <template #default>
      <AsyncComponent />
    </template>
    <template #fallback>
      <div>加载中...</div>
    </template>
  </Suspense>
</template>

<script setup lang="ts">
const AsyncComponent = defineAsyncComponent(() => 
  import('@/components/AsyncComponent.vue')
)
</script>
```

### 3. 图片懒加载

```vue
<template>
  <el-image :src="imageUrl" lazy />
</template>
```

## 测试指南

### 1. 单元测试

```typescript
// src/__tests__/NewComponent.spec.ts
import { mount } from '@vue/test-utils'
import NewComponent from '@/components/new/NewComponent.vue'

describe('NewComponent', () => {
  test('renders correctly', () => {
    const wrapper = mount(NewComponent)
    expect(wrapper.text()).toContain('New Component')
  })
})
```

### 2. 端到端测试

```typescript
// 使用 Cypress 或 Playwright
describe('New Page', () => {
  it('visits the new page', () => {
    cy.visit('/new')
    cy.contains('新页面')
  })
})
```

## 部署指南

### 本地部署

```bash
cd travel-agent-frontend
npm run build
npm run preview
```

## 常见问题

### 1. 跨域问题

配置Vite代理：

```typescript
// vite.config.ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

### 2. 类型错误

确保 `tsconfig.json` 配置正确

### 3. 组件未注册

检查组件路径和导入语句

## 贡献指南

1. Fork项目
2. 创建分支
3. 提交更改
4. 推送到分支
5. 提交Pull Request

## 许可证

MIT License
