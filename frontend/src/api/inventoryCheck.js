import request from '@/utils/request'

export function getInventoryCheckList(params) {
  return request({
    url: '/inventory-checks',
    method: 'get',
    params
  })
}

export function getInventoryCheck(id) {
  return request({
    url: `/inventory-checks/${id}`,
    method: 'get'
  })
}

export function deleteInventoryCheck(id, reason) {
  return request({
    url: `/inventory-checks/${id}`,
    method: 'delete',
    params: { reason }
  })
}

export function createInventoryCheck(data) {
  return request({
    url: '/inventory-checks',
    method: 'post',
    data
  })
}

export function completeInventoryCheck(id, data) {
  return request({
    url: `/inventory-checks/${id}/complete`,
    method: 'post',
    data
  })
}
