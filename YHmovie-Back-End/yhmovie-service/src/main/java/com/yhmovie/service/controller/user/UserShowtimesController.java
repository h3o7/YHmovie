package com.yhmovie.service.controller.user;

import com.yhmovie.common.annotation.RateLimit;
import com.yhmovie.pojo.dto.ShowtimesDto;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.pojo.vo.ShowtimesListVo;
import com.yhmovie.pojo.vo.ShowtimesOrderVo;
import com.yhmovie.pojo.vo.ShowtimesVo;
import com.yhmovie.service.service.IShowtimesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/showtimes")
public class UserShowtimesController {
    private final IShowtimesService showtimesService;

    @GetMapping("/list-least/{cinemaId}/{movieId}")
    public Result showtimesByCinemaIdAndMovieId(@PathVariable String cinemaId, @PathVariable String movieId) {
        Map<LocalDate, List<ShowtimesListVo>> map = showtimesService.showtimesByCinemaIdAndMovieId(cinemaId, movieId);
        return Result.success(map);
    }

    @GetMapping("/list-all")
    public Result showtimesOrderAll(@ModelAttribute ShowtimesDto showtimesDto) {
        List<ShowtimesOrderVo> list = showtimesService.showtimesOrderAll(showtimesDto);
        return Result.success(list);
    }

    @GetMapping("/info")
    public Result showtimesInfo(@ModelAttribute ShowtimesDto showtimesDto) {
        ShowtimesVo showtimesVo = showtimesService.showtimesInfo(showtimesDto);
        return Result.success(showtimesVo);
    }

    @RateLimit(key = "generateShowtimes",time = 60 * 60 * 24,count = 1,msg = "一天只能生成一次场次数据")
    @PostMapping("/generate-data/{cityId}")
    public Result generateShowtimesData(@PathVariable String cityId) {
        return showtimesService.generateShowtimesData(cityId);
    }


}
