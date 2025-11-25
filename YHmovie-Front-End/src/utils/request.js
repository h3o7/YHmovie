import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api', // 基础请求地址
  timeout: 10000, // 增加超时时间到10秒
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => { // 修正拼写：congig -> config
    console.log('发送请求:', config.url, config.data) // 添加调试日志
    
    // 给请求头添加token令牌（排除登录接口）
    const isLoginRequest = config.url?.includes('/login')
    
    if (!isLoginRequest) {
      try {
        const loginData = localStorage.getItem('login')
        if (loginData) {
          const login = JSON.parse(loginData)
          if (login && login.token) {
            config.headers['Authorization'] = `Bearer ${login.token}`
          }
        }
      } catch (error) {
        console.error('解析登录数据失败:', error)
        localStorage.removeItem('login') // 清除无效数据
      }
    }
    
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    console.log('收到响应:', response.config.url, response.data) // 添加调试日志
    return response.data // 直接返回数据部分
  },
  (error) => {
    console.error('响应拦截器捕获错误:', error)
    
    // 安全地处理错误响应
    if (error.response) {
      // 服务器返回了错误状态码
      const status = error.response.status
      const message = error.response.data?.message || '服务器错误'
      
      if (status === 401) {
        // 删除token
        localStorage.removeItem('login')
        // 只在非登录页面时跳转到登录页
        if (window.location.pathname !== '/login') {
          // 使用 window.location 而不是 router，避免导入问题
          window.location.href = '/login'
        }
        
        ElMessage.error('登录已过期，请重新登录')
      } else if (status === 403) {
        ElMessage.error('权限不足')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 500) {
        ElMessage.error('服务器内部错误')
      } else {
        ElMessage.error(message || `请求失败 (${status})`)
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应（网络错误）
      console.error('网络错误:', error.request)
      ElMessage.error('网络连接失败，请检查网络设置')
    } else {
      // 其他错误
      console.error('请求配置错误:', error.message)
      ElMessage.error(error.message || '请求失败')
    }
    
    return Promise.reject(error)
  }
)

export default request