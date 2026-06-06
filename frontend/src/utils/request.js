import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '@/store'

const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

service.interceptors.request.use(
  config => {
    const token = store.state.user.token
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    config.headers['X-Username'] = store.state.user.userInfo?.username || 'admin'
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200 && res.code !== 0) {
      Message.error(res.message || '请求失败')
      if (res.code === 401) {
        MessageBox.confirm('登录已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/logout')
          window.location.reload()
        })
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res.data !== undefined ? res.data : res
    }
  },
  error => {
    console.error('Response error:', error)
    Message.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
