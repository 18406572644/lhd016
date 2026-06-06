import request from '@/utils/request'

export function getOperationLogList(params) {
  return request({
    url: '/operation-logs',
    method: 'get',
    params
  })
}

export function getOperationLog(id) {
  return request({
    url: `/operation-logs/${id}`,
    method: 'get'
  })
}

export function getModuleOptions() {
  return request({
    url: '/operation-logs/modules',
    method: 'get'
  })
}

export function getOperationTypeOptions() {
  return request({
    url: '/operation-logs/operation-types',
    method: 'get'
  })
}
