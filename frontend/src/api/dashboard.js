import request from '@/utils/request'

export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

export function getCategoryStats() {
  return request({
    url: '/dashboard/category-stats',
    method: 'get'
  })
}

export function getHelpTrendStats() {
  return request({
    url: '/dashboard/help-trend',
    method: 'get'
  })
}
