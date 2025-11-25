import request from "@/utils/request"



// 用户端电影接口
// 正在热映电影
export const showingMovies = (pageNum,pageSize) => request.get(`/user/movies/showing/list?pageNum=${pageNum}&pageSize=${pageSize}`)
// 即将上映电影
export const comingMovies = (pageNum,pageSize) => request.get(`/user/movies/coming/list?pageNum=${pageNum}&pageSize=${pageSize}`)
// 根据影院id获取电影列表
export const moviesByCinemaId = (cinemaId) => request.get(`/user/movies/list/${cinemaId}`)

// 票房排行榜
export const boxOfficeRanking = (pageNum,pageSize) => request.get(`/user/movies/ranking/box-office?pageNum=${pageNum}&pageSize=${pageSize}`)
// 评分排行榜
export const ratingRanking = (pageNum,pageSize) => request.get(`/user/movies/ranking/rating?pageNum=${pageNum}&pageSize=${pageSize}`)
// 电影详情
export const movieDetailById = (movieId) => request.get(`/user/movies/detail/${movieId}`)

export const movieActors = (movieId) => request.get(`/user/movies/detail/actors/${movieId}`)

export const searchMovies = (keyWord) => request.get(`/user/movies/search?keyWord=${keyWord}`)
