import request from '@/utils/request'

export function getSupplyPointList(params) {
  return request({
    url: '/supply-points',
    method: 'get',
    params
  })
}

export function getSupplyPoint(id) {
  return request({
    url: `/supply-points/${id}`,
    method: 'get'
  })
}

export function deleteSupplyPoint(id, reason) {
  return request({
    url: `/supply-points/${id}`,
    method: 'delete',
    params: { reason }
  })
}

export function createSupplyPoint(data) {
  return request({
    url: '/supply-points',
    method: 'post',
    data
  })
}

export function updateSupplyPoint(id, data) {
  return request({
    url: `/supply-points/${id}`,
    method: 'put',
    data
  })
}

export function getSupplyPointOptions() {
  return request({
    url: '/supply-points/options',
    method: 'get'
  })
}
