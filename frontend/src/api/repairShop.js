import request from '@/utils/request'

export function getRepairShopList(params) {
  return request({
    url: '/repair-shops',
    method: 'get',
    params
  })
}

export function getRepairShop(id) {
  return request({
    url: `/repair-shops/${id}`,
    method: 'get'
  })
}

export function deleteRepairShop(id, reason) {
  return request({
    url: `/repair-shops/${id}`,
    method: 'delete',
    params: { reason }
  })
}

export function createRepairShop(data) {
  return request({
    url: '/repair-shops',
    method: 'post',
    data
  })
}

export function updateRepairShop(id, data) {
  return request({
    url: `/repair-shops/${id}`,
    method: 'put',
    data
  })
}
