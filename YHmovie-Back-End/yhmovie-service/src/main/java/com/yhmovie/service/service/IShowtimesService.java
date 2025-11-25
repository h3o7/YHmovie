package com.yhmovie.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.dto.ShowtimesDto;
import com.yhmovie.pojo.entity.Showtimes;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.pojo.vo.ShowtimesListVo;
import com.yhmovie.pojo.vo.ShowtimesOrderVo;
import com.yhmovie.pojo.vo.ShowtimesVo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 场次信息表 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface IShowtimesService extends IService<Showtimes> {


    Map<LocalDate, List<ShowtimesListVo>> showtimesByCinemaIdAndMovieId(String cinemaId, String movieId);

    ShowtimesVo showtimesInfo(ShowtimesDto showtimesDto);

    List<ShowtimesOrderVo> showtimesOrderAll(ShowtimesDto showtimesDto);

    // 管理端相关接口
    Result deleteMovieShowtimes();

    Result addMovieShowtimes(ShowtimesDto showtimesDto);
}
