package com.yhmovie.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhmovie.pojo.dto.ShowtimesDto;
import com.yhmovie.pojo.entity.Cinemas;
import com.yhmovie.pojo.entity.MovieHalls;
import com.yhmovie.pojo.entity.Movies;
import com.yhmovie.service.mapper.CinemaHallSeatsMapper;
import com.yhmovie.service.mapper.CinemasMapper;
import com.yhmovie.service.mapper.MovieHallsMapper;
import com.yhmovie.service.mapper.MoviesMapper;
import com.yhmovie.service.service.IShowtimesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

import java.util.List;

@SpringBootTest
public class GenerateShowtimesData {
    private final String CITY_ID = "500100"; // 重庆市
    private final Long INTERVAL_TIME_MINUTES = 150L; // 每场电影间隔150分钟
    private final LocalDateTime NOW_UPPER_TWO = LocalDateTime.now().plusHours(2); // 当前时间向后推2小时

    @Autowired
    private IShowtimesService showtimesService;
    @Autowired
    private CinemasMapper cinemasMapper;
    @Autowired
    private MoviesMapper moviesMapper;
    @Autowired
    private MovieHallsMapper movieHallsMapper;

    @Test
    void addShowtimes() {
        // 为某一个城市的所有影院添加放映场次（包含前后三天）
        // 1、查询该城市的所有影院
        List<String> cinemaIds = cinemasMapper.selectList(new LambdaQueryWrapper<>(Cinemas.class).eq(Cinemas::getCityId, CITY_ID))
                .stream().map(Cinemas::getCinemaId).toList();
        // 2、查询所有电影
        List<String> movieIds = moviesMapper.selectList(null).stream().map(Movies::getMovieId).toList();
        // 3、查询所有影厅
        List<String> movieHallIds = movieHallsMapper.selectList(null).stream().map(MovieHalls::getMovieHallId).toList();
        List<String> newMovieHallIds = movieHallIds.subList(0, new Random().nextInt(movieHallIds.size()));


        cinemaIds.forEach(cinemaId -> {
            newMovieHallIds.forEach(movieHallId -> {
                LocalDateTime endTime = NOW_UPPER_TWO.plusDays(3);
                LocalDateTime startTime = NOW_UPPER_TWO;
                while(startTime.isBefore(endTime)){
                    // 随机选择一部电影
                    String movieId = movieIds.get(new Random().nextInt(movieIds.size()));
                    // 随机生成票价 30-100
                    double price = 30 + (100 - 30) * new Random().nextDouble();
                    price = Math.round(price * 100.0) / 100.0; // 保留两位小数
                    ShowtimesDto showtimesDto = new ShowtimesDto(movieId, cinemaId, startTime.toLocalDate(), startTime.toLocalTime(), BigDecimal.valueOf(price), movieHallId);
                    showtimesService.addMovieShowtimes(showtimesDto);
                    // 计算下一场电影的开始时间
                    startTime = startTime.plusMinutes(INTERVAL_TIME_MINUTES);
                }
            });
        });

    }

}
