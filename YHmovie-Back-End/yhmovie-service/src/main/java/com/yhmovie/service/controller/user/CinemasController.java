package com.yhmovie.service.controller.user;

import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.vo.CinemasVo;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.ICinemasService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/cinemas")
public class CinemasController {

    private final ICinemasService cinemasService;

    @GetMapping("/list/{cityId}")
    public Result cinemasListByCityId(@PathVariable String cityId, @ModelAttribute PageRequest pageRequest) {
        PageResult<CinemasVo> list = cinemasService.getCinemasListByCityId(cityId, pageRequest.getPageNum(), pageRequest.getPageSize());
        return Result.success(list);
    }

    @GetMapping("/list/{cityId}/{movieId}")
    public Result cinemasListByCityIdAndMovieId(@PathVariable String cityId, @PathVariable String movieId, @ModelAttribute PageRequest pageRequest) {
        PageResult<CinemasVo> list = cinemasService.getCinemasListByCityIdAndMovieId(cityId,movieId, pageRequest.getPageNum(), pageRequest.getPageSize());
        return Result.success(list);
    }

    @GetMapping("/search/{cityId}")
    public Result searchCinemas(@PathVariable String cityId,@RequestParam String keyWord, @ModelAttribute PageRequest pageRequest) {
        PageResult<CinemasVo> list = cinemasService.search(cityId,keyWord, pageRequest.getPageNum(), pageRequest.getPageSize());
        return Result.success(list);
    }

}
