import request from '@/utils/request'

// 获取影院列表
export const showtimesLeastPrice = (cinemaId, movieId) => request.get(`/user/showtimes/list-least/${cinemaId}/${movieId}`)

export const seatsList = (showtimesDto) => request.get('/user/showtimes/list-all', {
    params: {
        movieId: showtimesDto.movieId,
        cinemaId: showtimesDto.cinemaId,
        movieHallId: showtimesDto.movieHallId,
        showtimeShowDate: showtimesDto.showtimeShowDate,
        showtimeStartTime: showtimesDto.showtimeStartTime
    }
})

export const showtimesInfo = (showtimesDto) => request.get('/user/showtimes/info', {
    params: {
        movieId: showtimesDto.movieId,
        cinemaId: showtimesDto.cinemaId,
        movieHallId: showtimesDto.movieHallId,
        showtimeShowDate: showtimesDto.showtimeShowDate,
        showtimeStartTime: showtimesDto.showtimeStartTime
    }
})



