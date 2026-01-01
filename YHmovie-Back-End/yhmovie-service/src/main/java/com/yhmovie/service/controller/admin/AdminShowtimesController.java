package com.yhmovie.service.controller.admin;

import com.yhmovie.pojo.dto.ShowtimesDto;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.IShowtimesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/showtimes")
@Slf4j(topic = "AdminShowtimesController")
public class AdminShowtimesController {
    private final IShowtimesService showtimesService;

    @PostMapping("/add")
    public Result addMovieShowtimes(@RequestBody ShowtimesDto showtimesDto) {
        return showtimesService.addMovieShowtimes(showtimesDto);

    }

    @DeleteMapping("/clean-data")
    public Result deleteMovieShowtimes() {
        log.info("删除无效场次");
        return showtimesService.deleteMovieShowtimes();
    }

}
