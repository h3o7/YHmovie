import request from '@/utils/request'

export const uploadUser = (file) => {
    const formData = new FormData()
    formData.append('file', file)
    
    return request.post('/upload/user-avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
}