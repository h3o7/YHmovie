package com.yhmovie.service.controller.user;

import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.vo.*;
import com.yhmovie.service.service.IMoviesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/movies")
@Slf4j
public class UserMoviesController {
    private final IMoviesService moviesService;

    @GetMapping("/showing/list")
    public Result showingMoviesList(@ModelAttribute PageRequest pageRequest) {
        PageResult<MoviesListVo> result = moviesService.showingMoviesList(pageRequest.getPageNum(), pageRequest.getPageSize());
        return Result.success(result);
    }

    @GetMapping("/coming/list")
    public Result comingMoviesList(@ModelAttribute PageRequest pageRequest) {
        log.debug("pageRequest: {}", pageRequest);
        PageResult<MoviesListVo> result = moviesService.comingMoviesList(pageRequest.getPageNum(), pageRequest.getPageSize());
        return Result.success(result);
    }

    @GetMapping("/list/{cinemaId}")
    public Result moviesListByCinemaId(@PathVariable String cinemaId) {
        PageResult<MoviesListVo> result = moviesService.moviesListByCinemaId(cinemaId);
        return Result.success(result);
    }

    @GetMapping("/ranking/box-office")
    public Result boxOfficeRanking(PageRequest pageRequest) {
        PageResult<MoviesListVo> result = moviesService.boxOfficeRanking(pageRequest);
        return Result.success(result);
    }

    @GetMapping("/ranking/rating")
    public Result ratingRanking(PageRequest pageRequest) {
        PageResult<MoviesListVo> result = moviesService.ratingRanking(pageRequest);
        return Result.success(result);
    }

    @GetMapping("/detail/{movieId}")
    public Result movieDetail(@PathVariable String movieId) {
        MoviesVo moviesVo = moviesService.movieDetail(movieId);
        return Result.success(moviesVo);
    }

    @GetMapping("/detail/actors/{movieId}")
    public Result movieActors(@PathVariable String movieId) {
        PageResult<MovieActorsVo> list = moviesService.movieActors(movieId);
        return Result.success(list);
    }

    @GetMapping("/search")
    public Result searchMovies(@RequestParam String keyWord, @ModelAttribute PageRequest pageRequest) {
        PageResult<MoviesListVo> result = moviesService.searchMovies(keyWord, pageRequest.getPageNum(), pageRequest.getPageSize());
        return Result.success(result);
    }
}
