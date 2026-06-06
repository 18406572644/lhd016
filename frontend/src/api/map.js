import request from '@/utils/request'

export function getMapOverview() {
  return request({
    url: '/map/overview',
    method: 'get'
  })
}
