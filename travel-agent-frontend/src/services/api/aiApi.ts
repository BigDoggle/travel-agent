# API Services Package
import api from '../api'

/**
 * AI API服务
 */
export const aiApi = {
  /**
   * 智能规划
   */
  plan: (data: any) => {
    return api.post('/ai/planner', data)
  },

  /**
   * 智能推荐
   */
  recommend: (data: any) => {
    return api.post('/ai/recommend', data)
  },

  /**
   * MCP交互
   */
  mcp: (data: any) => {
    return api.post('/ai/mcp', data)
  },

  /**
   * RAG检索
   */
  rag: (data: any) => {
    return api.post('/ai/rag', data)
  },

  /**
   * 工具调用
   */
  tool: (data: any) => {
    return api.post('/ai/tools', data)
  }
}
