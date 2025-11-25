import request from '@/utils/request'

// 用户端接口
export const getUser = (usersId) => request.get(`/user/users/info/${usersId}`)

export const updateUser = (usersDto) => request.put('/user/users/update',usersDto)