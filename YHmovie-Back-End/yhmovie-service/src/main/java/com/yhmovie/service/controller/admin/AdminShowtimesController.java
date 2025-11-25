package com.yhmovie.service.controller.admin;

import com.yhmovie.pojo.dto.ShowtimesDto;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.IShowtimesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/showtimes")
public class AdminShowtimesController {
    private final IShowtimesService showtimesService;

    @PostMapping("/add")
    public Result addMovieShowtimes(@RequestBody ShowtimesDto showtimesDto) {
        return showtimesService.addMovieShowtimes(showtimesDto);

    }

    @DeleteMapping
    public Result deleteMovieShowtimes() {
        return showtimesService.deleteMovieShowtimes();
    }

}
