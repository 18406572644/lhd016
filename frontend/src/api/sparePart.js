import request from '@/utils/request'

export function getSparePartList(params) {
  return request({
    url: '/spare-parts',
    method: 'get',
    params
  })
}

export function getSparePart(id) {
  return request({
    url: `/spare-parts/${id}`,
    method: 'get'
  })
}

export function createSparePart(data) {
  return request({
    url: '/spare-parts',
    method: 'post',
    data
  })
}

export function updateSparePart(id, data) {
  return request({
    url: `/spare-parts/${id}`,
    method: 'put',
    data
  })
}

export function deleteSparePart(id) {
  return request({
    url: `/spare-parts/${id}`,
    method: 'delete'
  })
}

export function getWarningList() {
  return request({
    url: '/spare-parts/warning',
    method: 'get'
  })
}

export function stockIn(id, data) {
  return request({
    url: `/spare-parts/${id}/stock-in`,
    method: 'post',
    data
  })
}

export function stockOut(id, data) {
  return request({
    url: `/spare-parts/${id}/stock-out`,
    method: 'post',
    data
  })
}

export function getStockRecords(id, params) {
  return request({
    url: `/spare-parts/${id}/records`,
    method: 'get',
    params
  })
}
