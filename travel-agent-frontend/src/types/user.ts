# Types Package
/**
 * 用户类型
 */
export interface User {
  id: number
  username: string
  email: string
  avatar?: string
  phone?: string
  status: number
  createdAt: string
  updatedAt: string
}
