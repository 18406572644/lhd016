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

export function deleteSupplyPoint(id) {
  return request({
    url: `/supply-points/${id}`,
    method: 'delete'
  })
}

export function getSupplyPointOptions() {
  return request({
    url: '/supply-points/options',
    method: 'get'
  })
}
