/**
 * 响应类型
 */
export interface Response<T = any> {
  code: number
  message: string
  data: T
}
