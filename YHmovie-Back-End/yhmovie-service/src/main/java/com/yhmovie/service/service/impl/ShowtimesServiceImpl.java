package com.yhmovie.service.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.yhmovie.common.exception.DBException;
import com.yhmovie.common.exception.ResourceNotFoundException;
import com.yhmovie.pojo.dto.ShowtimesDto;
import com.yhmovie.pojo.entity.*;
import com.yhmovie.pojo.vo.*;
import com.yhmovie.service.mapper.*;
import com.yhmovie.service.service.IMoviesService;
import com.yhmovie.service.service.IShowtimeExistsService;
import com.yhmovie.service.service.IShowtimesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 场次信息表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
@RequiredArgsConstructor
public class ShowtimesServiceImpl extends ServiceImpl<ShowtimesMapper, Showtimes> implements IShowtimesService {
    private final CinemaHallSeatsMapper cinemaHallSeatsMapper;
    private final IMoviesService moviesService;
    private final MovieHallsMapper movieHallsMapper;
    private final SeatsMapper seatsMapper;
    private final ShowtimeExistsMapper showtimeExistsMapper;
    private final CinemasMapper cinemasMapper;
    private final MoviesMapper moviesMapper;


    @Override
    public Map<LocalDate, List<ShowtimesListVo>> showtimesByCinemaIdAndMovieId(String cinemaId, String movieId) {
        List<Showtimes> showtimesList = baseMapper.selectList(new LambdaQueryWrapper<>(Showtimes.class)
                .eq(Showtimes::getCinemaId, cinemaId)
                .eq(Showtimes::getMovieId, movieId));
        if (ObjectUtil.isEmpty(showtimesList)) throw new ResourceNotFoundException("未找到对应的电影场次信息");

        TreeMap<LocalDate, List<ShowtimesListVo>> ResultMap = new TreeMap<>();

        // 根据日期分类
        TreeMap<LocalDate, List<Showtimes>> dateGroupMap = new TreeMap<>();  // 使用 TreeMap 按日期排序
        for (Showtimes showtimes : showtimesList) {
            LocalDate showDate = showtimes.getShowtimeShowDate();
            dateGroupMap.computeIfAbsent(showDate, k -> new ArrayList<>()).add(showtimes);
        }

        for (Map.Entry<LocalDate, List<Showtimes>> entry : dateGroupMap.entrySet()) {
            // 根据影厅号分类
            Map<String, List<Showtimes>> hallGroupMap = new HashMap<>();
            for (Showtimes showtimes : entry.getValue()) {
                String hallId = showtimes.getMovieHallId();
                hallGroupMap.computeIfAbsent(hallId, k -> new ArrayList<>()).add(showtimes);
            }
            List<ShowtimesListVo> list = new ArrayList<>(hallGroupMap.values().stream()
                    .map(sl -> {
                        ShowtimesListVo showtimesListVo = new ShowtimesListVo();
                        Showtimes showtimesLeast = null;
                        for (Showtimes showtimes : sl) {
                            if (showtimesLeast == null || showtimesLeast.getShowtimeTicketPrice().compareTo(showtimes.getShowtimeTicketPrice()) > 0) {
                                showtimesLeast = showtimes;
                            }
                        }
                        if (showtimesLeast == null) {
                            throw new RuntimeException("逻辑异常，未找到对应的最低价场次");
                        }
                        BeanUtils.copyProperties(showtimesLeast, showtimesListVo);
                        showtimesListVo.setLeastPrice(showtimesLeast.getShowtimeTicketPrice());
                        Byte hallNumber = movieHallsMapper.selectById(showtimesLeast.getMovieHallId()).getMovieHallNumber();
                        String language = moviesService.getById(showtimesLeast.getMovieId()).getMovieLanguage();
                        showtimesListVo.setMovieHallNumber(hallNumber);
                        showtimesListVo.setMovieLanguage(language);
                        return showtimesListVo;
                    }).toList());
            // 通过电影开始时间排序
            list.sort(Comparator.comparing(ShowtimesListVo::getShowtimeStartTime));
            ResultMap.put(entry.getKey(), list);
        }


        return ResultMap;
    }

    @Override
    public ShowtimesVo showtimesInfo(ShowtimesDto showtimesDto) {
        ShowtimesVo showtimesVo = new ShowtimesVo();
        // 添加场次的查询条件
        Map<SFunction<Showtimes, ?>, Object> columnMap = new HashMap<>();
        columnMap.put(Showtimes::getMovieId, showtimesDto.getMovieId());
        columnMap.put(Showtimes::getCinemaId, showtimesDto.getCinemaId());
        columnMap.put(Showtimes::getMovieHallId, showtimesDto.getMovieHallId());
        columnMap.put(Showtimes::getShowtimeShowDate, showtimesDto.getShowtimeShowDate());
        columnMap.put(Showtimes::getShowtimeStartTime, showtimesDto.getShowtimeStartTime());
        Showtimes showtimes = baseMapper.selectOne(new LambdaQueryWrapper<>(Showtimes.class)
                .allEq(columnMap).last("LIMIT 1"));
        if(ObjectUtil.isEmpty(showtimes)) throw new ResourceNotFoundException("未找到对应的电影场次信息");
        BeanUtils.copyProperties(showtimes, showtimesVo);
        MoviesVo moviesVo = moviesService.movieDetail(showtimes.getMovieId());
        BeanUtils.copyProperties(moviesVo, showtimesVo);
        showtimesVo.setCinemaName(cinemasMapper.selectById(showtimes.getCinemaId()).getCinemaName());
        showtimesVo.setMovieHallNumber(movieHallsMapper.selectById(showtimes.getMovieHallId()).getMovieHallNumber());
        return showtimesVo;
    }

    @Override
    public List<ShowtimesOrderVo> showtimesOrderAll(ShowtimesDto showtimesDto) {
        // 添加场次的查询条件
        Map<SFunction<Showtimes, ?>, Object> columnMap = new HashMap<>();
        columnMap.put(Showtimes::getMovieId, showtimesDto.getMovieId());
        columnMap.put(Showtimes::getCinemaId, showtimesDto.getCinemaId());
        columnMap.put(Showtimes::getMovieHallId, showtimesDto.getMovieHallId());
        columnMap.put(Showtimes::getShowtimeShowDate, showtimesDto.getShowtimeShowDate());
        columnMap.put(Showtimes::getShowtimeStartTime, showtimesDto.getShowtimeStartTime());
        List<Showtimes> showtimesList = baseMapper.selectList(new LambdaQueryWrapper<>(Showtimes.class).allEq(columnMap));
        if (ObjectUtil.isEmpty(showtimesList)) throw new ResourceNotFoundException("未找到对应的电影场次信息");

        List<ShowtimesOrderVo> list = showtimesList.stream().map(sl -> {
            ShowtimesOrderVo showtimesOrderVo = new ShowtimesOrderVo();
            BeanUtils.copyProperties(sl, showtimesOrderVo);
            Seats seat = seatsMapper.selectById(cinemaHallSeatsMapper.selectById(sl.getCinemaHallSeatId()).getSeatId());
            showtimesOrderVo.setSeatNumber(seat.getSeatRow() + "排" + seat.getSeatCol() + "座");
            ShowtimeExists se = showtimeExistsMapper.selectOne(new LambdaQueryWrapper<>(ShowtimeExists.class).eq(ShowtimeExists::getShowtimeId, sl.getShowtimeId()));
            showtimesOrderVo.setSoldStatus(!ObjectUtil.isEmpty(se));
            return showtimesOrderVo;
        }).toList();
        List<ShowtimesOrderVo> sortedList = list.stream()
                .sorted((a, b) -> {
                    // 解析座位号，如"5排2座"
                    String seatA = a.getSeatNumber();
                    String seatB = b.getSeatNumber();

                    // 提取行号
                    int rowA = Integer.parseInt(seatA.substring(0, seatA.indexOf("排")));
                    int rowB = Integer.parseInt(seatB.substring(0, seatB.indexOf("排")));

                    // 先按行号排序
                    if (rowA != rowB) {
                        return rowA - rowB;
                    }

                    // 行号相同时按列号排序
                    int colA = Integer.parseInt(seatA.substring(seatA.indexOf("排") + 1, seatA.indexOf("座")));
                    int colB = Integer.parseInt(seatB.substring(seatB.indexOf("排") + 1, seatB.indexOf("座")));

                    return colA - colB;
                }).toList();
        return sortedList;
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result generateShowtimesData(String cityId) {
        String CITY_ID = cityId;
        Long INTERVAL_TIME_MINUTES = 150L;
        LocalDateTime NOW_UPPER_TWO = LocalDateTime.now().plusHours(2);
        // 1、查询该城市的所有影院
        List<String> cinemaIds = cinemasMapper.selectList(new LambdaQueryWrapper<>(Cinemas.class).eq(Cinemas::getCityId, CITY_ID))
                .stream().map(Cinemas::getCinemaId).toList();
        if(cinemaIds.isEmpty()){
            return Result.error("该城市暂无影院，无法生成电影场次数据");
        }
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
                    addMovieShowtimes(showtimesDto);
                    // 计算下一场电影的开始时间
                    startTime = startTime.plusMinutes(INTERVAL_TIME_MINUTES);
                }
            });
        });
        return Result.success("生成数据成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addMovieShowtimes(ShowtimesDto showtimesDto) {
        List<CinemaHallSeats> cinemaHallSeatsList = cinemaHallSeatsMapper.selectList(new LambdaQueryWrapper<>(CinemaHallSeats.class)
                .eq(CinemaHallSeats::getCinemaId, showtimesDto.getCinemaId())
                .eq(CinemaHallSeats::getMovieHallId, showtimesDto.getMovieHallId()));
        if (ObjectUtil.isEmpty(cinemaHallSeatsList)) return Result.error("未找到对应的影厅座位信息，无法添加电影场次");
        List<String> cinameHallSeatsIdList = cinemaHallSeatsList.stream().map(CinemaHallSeats::getCinemaHallSeatId).toList();
        List<Showtimes> list = cinameHallSeatsIdList.stream()
                .map(chs -> {
                    Showtimes showtimes = new Showtimes();
                    BeanUtils.copyProperties(showtimesDto, showtimes);
                    showtimes.setShowtimeId(IdUtil.simpleUUID());
                    showtimes.setCinemaHallSeatId(chs);
                    Integer duration = moviesService.getById(showtimesDto.getMovieId()).getMovieDuration();
                    showtimes.setShowtimeEndTime(showtimesDto.getShowtimeStartTime().plusMinutes(duration));
                    showtimes.setCreateTime(LocalDateTime.now());
                    showtimes.setUpdateTime(LocalDateTime.now());
                    return showtimes;
                }).toList();

        boolean flag = saveBatch(list);
        if (!flag) throw new DBException("数据库异常，添加电影场次失败");

        return Result.success("电影场次添加成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteMovieShowtimes() {
        // 根据当前时间删除过期的场次信息
        int rows = baseMapper.delete(new LambdaQueryWrapper<Showtimes>()
                .lt(Showtimes::getShowtimeShowDate, LocalDate.now())
                .or()
                .eq(Showtimes::getShowtimeShowDate, LocalDate.now())
                .lt(Showtimes::getShowtimeEndTime, LocalDateTime.now()));
        if(rows == 0) return Result.success("暂无过期的电影场次信息需要删除");
        return Result.success("成功删除过期的电影场次信息，共删除 " + rows + " 条记录");
    }
}
