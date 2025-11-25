import request from '@/utils/request'

export const cinemasListByCityId = (cityId,pageNum,pageSize) => request.get(`user/cinemas/list/${cityId}?pageNum=${pageNum}&pageSize=${pageSize}`)

export const cinemasListByCityIdAndMovieId = (cityId, movieId,pageNum,pageSize) => request.get(`user/cinemas/list/${cityId}/${movieId}?pageNum=${pageNum}&pageSize=${pageSize}`)

export const searchCinemas = (keyWord,cityId) => request.get(`/user/cinemas/search/${cityId}?keyWord=${keyWord}`)