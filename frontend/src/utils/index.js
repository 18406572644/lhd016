import moment from 'moment'

export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  return moment(date).format(format)
}

export function formatDateSimple(date) {
  return formatDate(date, 'YYYY-MM-DD')
}

export function getStatusType(status) {
  const map = {
    '正常': 'success',
    '维护中': 'warning',
    '关闭': 'info',
    'pending': 'warning',
    'processing': 'primary',
    'completed': 'success',
    'draft': 'info'
  }
  return map[status] || 'primary'
}

export function getStatusText(status) {
  const map = {
    'pending': '待处理',
    'processing': '处理中',
    'completed': '已完成',
    'draft': '草稿'
  }
  return map[status] || status
}

export function getUrgencyType(urgency) {
  const map = {
    '低': 'info',
    '中': 'success',
    '高': 'warning',
    '紧急': 'danger'
  }
  return map[urgency] || 'primary'
}

export function getLevelType(level) {
  const map = {
    '一级': 'danger',
    '二级': 'warning',
    '三级': 'info'
  }
  return map[level] || 'primary'
}

export function isLowStock(stock, threshold) {
  return stock <= threshold
}

export function generateCheckNo() {
  return 'PD' + moment().format('YYYYMMDDHHmmss')
}

export function debounce(func, wait) {
  let timeout
  return function(...args) {
    clearTimeout(timeout)
    timeout = setTimeout(() => func.apply(this, args), wait)
  }
}
