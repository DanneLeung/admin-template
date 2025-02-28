// src/utils/request.js
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from './auth'
import { useUserStore } from '../store/user'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 60000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token到请求头
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  // 处理成功响应
  response => {
    const res = response.data
    
    // 根据自定义错误码处理错误
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })

      // 处理特定错误码
      // 401: 未登录或Token过期
      if (res.code === 401) {
        handleUnauthorized()
      }
      
      // 403: 权限不足
      if (res.code === 403) {
        ElMessage({
          message: '权限不足，无法执行此操作',
          type: 'error',
          duration: 5 * 1000
        })
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  // 处理错误响应
  error => {
    const { response } = error
    
    if (response) {
      // 根据HTTP状态码处理
      switch (response.status) {
        case 401:
          handleUnauthorized()
          break
        case 403:
          ElMessage({
            message: '权限不足，无法执行此操作',
            type: 'error',
            duration: 5 * 1000
          })
          break
        case 404:
          ElMessage({
            message: '请求的资源不存在',
            type: 'error',
            duration: 5 * 1000
          })
          break
        case 500:
          ElMessage({
            message: '服务器内部错误',
            type: 'error',
            duration: 5 * 1000
          })
          break
        default:
          ElMessage({
            message: '请求失败',
            type: 'error',
            duration: 5 * 1000
          })
      }
    } else {
      // 处理网络错误
      ElMessage({
        message: '网络错误，请检查您的网络连接',
        type: 'error',
        duration: 5 * 1000
      })
    }
    return Promise.reject(error)
  }
)

// 处理401未授权
function handleUnauthorized() {
  ElMessageBox.confirm(
    '登录状态已失效，您可以继续留在该页面，或者重新登录',
    '系统提示',
    {
      confirmButtonText: '重新登录',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 清空用户状态并跳转到登录页
    const userStore = useUserStore()
    userStore.logout().then(() => {
      // 重定向到登录页
      location.reload()
    })
  }).catch(() => {
    // 用户取消，不做任何处理
  })
}

export default service