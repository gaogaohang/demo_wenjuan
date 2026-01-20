import axios, { AxiosInstance, AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios'
import { showFailToast, showSuccessToast } from 'vant'
import router from '@/router'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

apiClient.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    
    if (code === 200) {
      return { data }
    } else {
      showFailToast(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          showFailToast('登录已过期，请重新登录')
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 403:
          showFailToast(data?.message || '没有权限')
          break
        case 404:
          showFailToast('请求的资源不存在')
          break
        case 500:
          showFailToast('服务器错误，请稍后重试')
          break
        default:
          showFailToast(data?.message || '请求失败')
      }
    } else if (error.request) {
      showFailToast('网络连接失败，请检查网络')
    } else {
      showFailToast('请求失败')
    }
    
    return Promise.reject(error)
  }
)

export const request = <T>(config: AxiosRequestConfig): Promise<T> => {
  return apiClient.request<T>(config).then((response) => response.data)
}

export default apiClient
