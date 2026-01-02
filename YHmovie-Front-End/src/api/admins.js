import request from '@/utils/request'

export const getAdmin = (adminsId) => request.get(`/admins/${adminsId}`)

export const getAdminVOInfo = () => request.get('/admins/info') // 未实现的接口