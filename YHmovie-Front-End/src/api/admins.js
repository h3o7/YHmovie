import request from '@/utils/request'

export const getAdmin = (adminsId) => request.get(`/admins/${adminsId}`)