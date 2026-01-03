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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
@Slf4j(topic = "ShowtimesServiceImpl")
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
    // 1. 移除这里的 @Transactional，因为主方法只做查询且立刻返回，无需事务
    public Result generateShowtimesData(String cityId) {
        String CITY_ID = cityId;

        // 1、查询该城市的所有影院 ID (这是一个快速查询，可以阻塞等待)
        List<String> cinemaIds = cinemasMapper.selectList(new LambdaQueryWrapper<>(Cinemas.class)
                        .eq(Cinemas::getCityId, CITY_ID))
                .stream().map(Cinemas::getCinemaId).toList();

        if (cinemaIds.isEmpty()) {
            return Result.error("该城市暂无影院，无法生成电影场次数据");
        }

        // 开启一个新线程在后台处理数据，主线程立刻向下执行返回结果
        CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();
            log.info("开始后台生成场次数据，城市ID: {}", CITY_ID);

            try {
                generateDataTask(CITY_ID, cinemaIds); // 执行耗时逻辑
                long end = System.currentTimeMillis();
                log.info("后台生成电影场次数据完成，耗时 {} 毫秒", end - start);
            } catch (Exception e) {
                log.error("后台生成电影场次数据失败", e);
                // 这里可以添加逻辑：比如发送邮件通知管理员，或者更新某个任务状态表为失败
            }
        });

        // --- 【立即返回】 ---
        return Result.success("生成任务已提交，后台正在处理中，请稍后查看数据");
    }

    /**
     * 将耗时的业务逻辑抽取为独立方法
     * 注意：由于是在异步线程中执行，这里的事务建议按批次提交，或者在这个方法上加事务（如果需要整体回滚）
     * 考虑到数据量大，建议不加 @Transactional 大事务，而是依赖 saveBatch 的分批事务，避免长时间锁表
     */
    private void generateDataTask(String cityId, List<String> cinemaIds) {
        Long INTERVAL_TIME_MINUTES = 150L;
        LocalDateTime NOW_UPPER_TWO = LocalDateTime.now().plusHours(2);

        // 2、查询所有电影，并转为 Map (优化点1)
        List<Movies> moviesList = moviesMapper.selectList(null);
        if (moviesList.isEmpty()) {
            log.warn("暂无电影数据，停止生成");
            return;
        }

        Map<String, Integer> movieDurationMap = moviesList.stream()
                .collect(Collectors.toMap(Movies::getMovieId, Movies::getMovieDuration));
        List<String> movieIds = new ArrayList<>(movieDurationMap.keySet());

        // 3、查询所有影厅
        List<String> allMovieHallIds = movieHallsMapper.selectList(null)
                .stream().map(MovieHalls::getMovieHallId).toList();

        if (allMovieHallIds.isEmpty()) {
            log.warn("暂无影厅数据，停止生成");
            return;
        }

        // 随机选取一部分影厅
        List<String> targetMovieHallIds = allMovieHallIds.subList(0, Math.max(1, new Random().nextInt(allMovieHallIds.size())));

        // 准备一个大集合用于批量插入
        List<Showtimes> batchInsertList = new ArrayList<>();
        int BATCH_SIZE = 2000; // 每2000条插入一次 (优化点3)

        for (String cinemaId : cinemaIds) {
            for (String movieHallId : targetMovieHallIds) {

                // 4、在时间循环外，获取当前影厅的所有座位 ID (优化点2)
                List<CinemaHallSeats> seats = cinemaHallSeatsMapper.selectList(new LambdaQueryWrapper<>(CinemaHallSeats.class)
                        .eq(CinemaHallSeats::getCinemaId, cinemaId)
                        .eq(CinemaHallSeats::getMovieHallId, movieHallId));

                if (seats.isEmpty()) continue; // 如果该厅没座位，跳过
                List<String> seatIds = seats.stream().map(CinemaHallSeats::getCinemaHallSeatId).toList();

                LocalDateTime endTime = NOW_UPPER_TWO.plusDays(3);
                LocalDateTime startTime = NOW_UPPER_TWO;

                while (startTime.isBefore(endTime)) {
                    // 随机选择一部电影
                    String movieId = movieIds.get(new Random().nextInt(movieIds.size()));
                    Integer duration = movieDurationMap.get(movieId);

                    // 随机生成票价
                    double price = 30 + (100 - 30) * new Random().nextDouble();
                    BigDecimal finalPrice = BigDecimal.valueOf(Math.round(price * 100.0) / 100.0);

                    LocalDateTime showEndTime = startTime.plusMinutes(duration != null ? duration : 120);
                    LocalDateTime finalStartTime = startTime;

                    // 5、在内存中构建对象
                    for (String seatId : seatIds) {
                        Showtimes showtimes = new Showtimes();
                        showtimes.setShowtimeId(IdUtil.simpleUUID());
                        showtimes.setMovieId(movieId);
                        showtimes.setCinemaId(cinemaId);
                        showtimes.setMovieHallId(movieHallId);
                        showtimes.setShowtimeShowDate(finalStartTime.toLocalDate());
                        showtimes.setShowtimeStartTime(finalStartTime.toLocalTime());
                        showtimes.setShowtimeTicketPrice(finalPrice);
                        showtimes.setCinemaHallSeatId(seatId);
                        showtimes.setShowtimeEndTime(showEndTime.toLocalTime());
                        showtimes.setCreateTime(LocalDateTime.now());
                        showtimes.setUpdateTime(LocalDateTime.now());

                        batchInsertList.add(showtimes);
                    }

                    // 6、达到阈值，进行批量入库
                    if (batchInsertList.size() >= BATCH_SIZE) {
                        saveBatch(batchInsertList); // MyBatis-Plus 的 saveBatch 默认是分批事务
                        batchInsertList.clear();
                    }

                    // 计算下一场时间
                    startTime = startTime.plusMinutes(INTERVAL_TIME_MINUTES);
                }
            }
        }

        // 7、处理剩余的数据
        if (!batchInsertList.isEmpty()) {
            saveBatch(batchInsertList);
        }
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
