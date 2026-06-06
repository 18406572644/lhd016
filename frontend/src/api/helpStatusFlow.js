import request from '@/utils/request'

export function getStatusFlows(helpRequestId) {
  return request({
    url: `/help-status-flows/by-help-request/${helpRequestId}`,
    method: 'get'
  })
}
