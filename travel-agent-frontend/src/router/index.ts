# Router Package
import { createRouter, createWebHistory } from 'vue-router'

/**
 * 路由配置
 */
const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/trip',
    name: 'Trip',
    component: () => import('@/views/trip/TripView.vue'),
    meta: { title: '行程规划' }
  },
  {
    path: '/trip/:id',
    name: 'TripDetail',
    component: () => import('@/views/trip/TripDetailView.vue'),
    meta: { title: '行程详情' },
    props: true
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/ProfileView.vue'),
    meta: { title: '个人中心' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterView.vue'),
    meta: { title: '注册' }
  }
]

/**
 * 创建路由实例
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

/**
 * 路由守卫
 */
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - AI智能旅行规划系统`
  }
  next()
})

export default router
