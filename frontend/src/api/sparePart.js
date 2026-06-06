import request from '@/utils/request'

export function getSparePartList(params) {
  return request({
    url: '/spare-parts',
    method: 'get',
    params
  })
}

export function deleteSparePart(id, reason) {
  return request({
    url: `/spare-parts/${id}`,
    method: 'delete',
    params: { reason }
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
    data,
    params: { reason: data.reason }
  })
}

export function stockOut(id, data) {
  return request({
    url: `/spare-parts/${id}/stock-out`,
    method: 'post',
    data,
    params: { reason: data.reason }
  })
}

export function getStockRecords(id, params) {
  return request({
    url: `/spare-parts/${id}/records`,
    method: 'get',
    params
  })
}
