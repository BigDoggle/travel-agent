# API Services Package
import api from '../api'

/**
 * 行程API服务
 */
export const tripApi = {
  /**
   * 创建行程
   */
  create: (data: any) => {
    return api.post('/trips', data)
  },

  /**
   * 获取行程列表
   */
  list: (params?: { page?: number; size?: number }) => {
    return api.get('/trips', { params })
  },

  /**
   * 获取行程详情
   */
  get: (id: string) => {
    return api.get(`/trips/${id}`)
  },

  /**
   * 更新行程
   */
  update: (id: string, data: any) => {
    return api.put(`/trips/${id}`, data)
  },

  /**
   * 删除行程
   */
  delete: (id: string) => {
    return api.delete(`/trips/${id}`)
  },

  /**
   * 生成行程计划
   */
  generatePlan: (id: string) => {
    return api.post(`/trips/${id}/plan`)
  }
}
