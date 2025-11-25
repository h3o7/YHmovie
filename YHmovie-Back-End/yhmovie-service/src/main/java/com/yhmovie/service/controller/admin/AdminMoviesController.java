package com.yhmovie.service.controller.admin;

import com.yhmovie.pojo.dto.MoviesDto;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.IMoviesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/movies")
public class AdminMoviesController {
    private final IMoviesService moviesService;

    @PostMapping("/add")
    public Result addMovie(@RequestBody MoviesDto movieDto) {
        return moviesService.addMovie(movieDto);
    }

    @PutMapping("/update")
    public Result updateMovie(@RequestBody MoviesDto movieDto) {
        return moviesService.updateMovie(movieDto);
    }

    @DeleteMapping("/delete/{movieId}")
    public Result deleteMovie(@PathVariable String movieId) {
        return moviesService.deleteMovie(movieId);
    }


}
