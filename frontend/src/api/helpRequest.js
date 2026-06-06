import request from '@/utils/request'

export function getHelpRequestList(params) {
  return request({
    url: '/help-requests',
    method: 'get',
    params
  })
}

export function getHelpRequestListWithShop(params) {
  return request({
    url: '/help-requests/with-shop',
    method: 'get',
    params
  })
}

export function deleteHelpRequest(id, reason) {
  return request({
    url: `/help-requests/${id}`,
    method: 'delete',
    params: { reason }
  })
}



export function getHelpRequestWithShop(id) {
  return request({
    url: `/help-requests/${id}/with-shop`,
    method: 'get'
  })
}

export function getRecommendedShops(params) {
  return request({
    url: '/help-requests/recommend-shops',
    method: 'get',
    params
  })
}

export function createHelpRequest(data) {
  return request({
    url: '/help-requests',
    method: 'post',
    data
  })
}

export function dispatchHelpRequest(data) {
  return request({
    url: '/help-requests/dispatch',
    method: 'post',
    data
  })
}

export function adjustDispatch(id, repairShopId, notify) {
  return request({
    url: `/help-requests/${id}/adjust-dispatch`,
    method: 'put',
    params: { repairShopId, notify }
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
