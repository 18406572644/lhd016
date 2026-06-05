import request from '@/utils/request'

export function getHelpRequestList(params) {
  return request({
    url: '/help-requests',
    method: 'get',
    params
  })
}

export function getHelpRequest(id) {
  return request({
    url: `/help-requests/${id}`,
    method: 'get'
  })
}

export function createHelpRequest(data) {
  return request({
    url: '/help-requests',
    method: 'post',
    data
  })
}

export function updateHelpStatus(id, status) {
  return request({
    url: `/help-requests/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function handleHelpRequest(id, data) {
  return request({
    url: `/help-requests/${id}/handle`,
    method: 'put',
    data
  })
}

export function deleteHelpRequest(id) {
  return request({
    url: `/help-requests/${id}`,
    method: 'delete'
  })
}
