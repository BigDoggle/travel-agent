# API Services Package
import api from '../api'

/**
 * 用户API服务
 */
export const userApi = {
  /**
   * 用户登录
   */
  login: (data: { username: string; password: string }) => {
    return api.post('/auth/login', data)
  },

  /**
   * 用户注册
   */
  register: (data: { username: string; email: string; password: string }) => {
    return api.post('/auth/register', data)
  },

  /**
   * 获取用户信息
   */
  getProfile: () => {
    return api.get('/user/profile')
  },

  /**
   * 更新用户信息
   */
  updateProfile: (data: any) => {
    return api.put('/user/profile', data)
  }
}
