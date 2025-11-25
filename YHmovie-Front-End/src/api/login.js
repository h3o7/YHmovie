import request from '@/utils/request'

// 获取验证码 - 返回图片流
export const getCaptcha = () => request.get('/login/captcha', { responseType: 'blob' })

// 用户登录
export const userLogin = (loginForm) => request.post('/login/users', loginForm)

// 管理员登录
export const adminLogin = (loginForm) => request.post('/login/admins', loginForm)


// 首页接口
export const provincesList = () => request.get('/user/provinces/list')

export const citiesList = (provinceId) => request.get(`/user/cities/list/${provinceId}`)

export const searchCities = (cityName) => request.get(`/user/cities/search?cityName=${cityName}`)
