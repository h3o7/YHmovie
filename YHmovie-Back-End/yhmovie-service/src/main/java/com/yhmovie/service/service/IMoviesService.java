package com.yhmovie.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.dto.MoviesDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.entity.Movies;
import com.yhmovie.pojo.vo.*;

/**
 * <p>
 * 电影信息表 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface IMoviesService extends IService<Movies> {

    PageResult<MoviesListVo> showingMoviesList(Integer pageNum, Integer pageSize);

    PageResult<MoviesListVo> comingMoviesList(Integer pageNum, Integer pageSize);

    PageResult<MoviesListVo> moviesListByCinemaId(String cinemaId);

    MoviesVo movieDetail(String movieId);

    PageResult<MovieActorsVo> movieActors(String movieId);

    PageResult<MoviesListVo> boxOfficeRanking(PageRequest pageRequest);

    PageResult<MoviesListVo> ratingRanking(PageRequest pageRequest);

    PageResult<MoviesListVo> searchMovies(String movieName, Integer pageNum, Integer pageSize);

    // 管理端相关接口
    Result addMovie(MoviesDto movieDto);

    Result updateMovie(MoviesDto movieDto);

    Result deleteMovie(String movieId);


}
