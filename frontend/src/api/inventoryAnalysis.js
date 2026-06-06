import request from '@/utils/request'

export function getInventoryOverview() {
  return request({
    url: '/inventory-analysis/overview',
    method: 'get'
  })
}

export function getTurnoverRate(params) {
  return request({
    url: '/inventory-analysis/turnover-rate',
    method: 'get',
    params
  })
}

export function getAbcClassification() {
  return request({
    url: '/inventory-analysis/abc-classification',
    method: 'get'
  })
}

export function getValueDistribution(dimension) {
  return request({
    url: '/inventory-analysis/value-distribution',
    method: 'get',
    params: { dimension }
  })
}

export function getInOutTrend(params) {
  return request({
    url: '/inventory-analysis/inout-trend',
    method: 'get',
    params
  })
}

export function getHealthScore() {
  return request({
    url: '/inventory-analysis/health-score',
    method: 'get'
  })
}

export function getSupplyPointComparison() {
  return request({
    url: '/inventory-analysis/supply-point-comparison',
    method: 'get'
  })
}
