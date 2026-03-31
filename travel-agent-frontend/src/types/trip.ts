# Types Package
/**
 * 行程类型
 */
export interface Trip {
  id: number
  userId: number
  title: string
  description?: string
  destination?: string
  tripType?: string
  startDate?: string
  endDate?: string
  budget?: number
  budgetLevel?: string
  satisfactionScore?: number
  status: string
  createdAt: string
  updatedAt: string
}
