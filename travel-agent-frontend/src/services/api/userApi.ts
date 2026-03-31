import api from '../api'

/**
 * 用户API服务
 */
export const userApi = {
  /**
   * 用户登录
   */
  login: (data: { username: string; password: string }) => {
    return api.post('/users/login', data)
  },

  /**
   * 用户注册
   */
  register: (data: { username: string; email: string; password: string }) => {
    return api.post('/users/register', data)
  },

  /**
   * 获取用户信息
   */
  getProfile: (id: number) => {
    return api.get(`/users/${id}`)
  },

  /**
   * 更新用户信息
   */
  updateProfile: (id: number, data: any) => {
    return api.put(`/users/${id}`, data)
  }
}