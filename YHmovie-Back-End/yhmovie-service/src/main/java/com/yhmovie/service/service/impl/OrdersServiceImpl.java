package com.yhmovie.service.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yhmovie.common.exception.DBException;
import com.yhmovie.common.util.CurrentHolder;
import com.yhmovie.pojo.entity.*;
import com.yhmovie.pojo.vo.OrdersVo;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.mapper.*;
import com.yhmovie.service.service.ICinemaHallSeatsService;
import com.yhmovie.service.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhmovie.service.service.IShowtimeExistsService;
import com.yhmovie.service.service.IShowtimesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 电影订单表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
@RequiredArgsConstructor
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    private final IShowtimesService showtimesService;
    private final ICinemaHallSeatsService cinemaHallSeatsService;
    private final IShowtimeExistsService showtimeExistsService;
    private final MoviesMapper moviesMapper;
    private final CinemasMapper cinemasMapper;
    private final MovieHallsMapper movieHallsMapper;
    private final SeatsMapper seatsMapper;
    private final ShowtimesMapper showtimesMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result buyTickets(List<String> showtimesIds) {
        if(showtimesIds.isEmpty()) return Result.error("未选择任何座位");
        showtimesIds.forEach(showtimesId -> {
            if(showtimeExistsService.count(new LambdaQueryWrapper<>(ShowtimeExists.class)
                    .eq(ShowtimeExists::getShowtimeId, showtimesId)) > 0) {
                throw new DBException("部分座位已被抢先购买，请重新选择");
            }
        });
        String userId = CurrentHolder.getCurrentId();
        Orders order = new Orders();
        order.setOrderId(IdUtil.simpleUUID());
        BeanUtil.copyProperties(showtimesService.getById(showtimesIds.get(0)), order);
        order.setUserId(userId);

        // 构建 ShowtimeExists 列表
        List<ShowtimeExists> list = showtimesIds.stream().map(si -> {
            ShowtimeExists se = new ShowtimeExists();
            Showtimes s = showtimesService.getById(si);
            se.setShowtimeExitId(IdUtil.simpleUUID());
            BeanUtil.copyProperties(s, se);
            String seatId = cinemaHallSeatsService.getById(s.getCinemaHallSeatId()).getSeatId();
            se.setSeatId(seatId);
            se.setOrderId(order.getOrderId());
            return se;
        }).toList();
        // 计算总价
        BigDecimal totalPrice = showtimesIds.stream()
                .map(id -> showtimesService.getById(id).getShowtimeTicketPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setOrderTotalPrice(totalPrice);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        // 更新或插入数据
        boolean saveOrder = save(order);
        boolean saveSE = showtimeExistsService.saveBatch(list);
        if(!saveOrder || !saveSE) throw new DBException("数据更新异常，购票失败，请稍后重试");
        int updateM = moviesMapper.update(new LambdaUpdateWrapper<>(Movies.class)
                .eq(Movies::getMovieId, order.getMovieId())
                .setIncrBy(Movies::getMovieBoxOffice, totalPrice.doubleValue()));
        if(updateM != 1) throw new DBException("数据更新异常，购票失败，请稍后重试");
        return Result.success("购票成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result returnTickets(String orderId) {
        String userId = CurrentHolder.getCurrentId();
        Orders order = getById(orderId);
        if(ObjectUtil.isEmpty(order) || !order.getUserId().equals(userId))
            return Result.error("订单不存在");

        // 删除订单的相关数据
        boolean removeSE = showtimeExistsService.remove(new LambdaQueryWrapper<>(ShowtimeExists.class)
                .eq(ShowtimeExists::getOrderId, orderId));
        boolean removeOrder = removeById(orderId);
        int updateM = moviesMapper.update(new LambdaUpdateWrapper<>(Movies.class)
                .eq(Movies::getMovieId, order.getMovieId())
                .setDecrBy(Movies::getMovieBoxOffice, order.getOrderTotalPrice().doubleValue()));
        if(!removeSE || !removeOrder || updateM != 1) throw new DBException("数据更新异常，退票失败，请稍后重试");
        return Result.success("退票成功");
    }

    @Override
    public List<OrdersVo> userOrders() {
        String userId = CurrentHolder.getCurrentId();
        List<Orders> orders = baseMapper.selectList(new LambdaQueryWrapper<>(Orders.class)
                .eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreateTime));
        if(ObjectUtil.isEmpty(orders)) return null;
        List<OrdersVo> list = orders.stream().map(order -> {
            OrdersVo vo = new OrdersVo();
            vo.setOrderId(order.getOrderId());
            vo.setMovieName(moviesMapper.selectById(order.getMovieId()).getMovieName());
            vo.setMoviePosterUrl(moviesMapper.selectById(order.getMovieId()).getMoviePosterUrl());
            vo.setCinemaName(cinemasMapper.selectById(order.getCinemaId()).getCinemaName());
            vo.setMovieHallNumber((int) movieHallsMapper.selectById(order.getMovieHallId()).getMovieHallNumber());
            vo.setCreateTime(order.getCreateTime());
            vo.setOrderTotalPrice(order.getOrderTotalPrice());
            List<ShowtimeExists> seList = showtimeExistsService.list(new LambdaQueryWrapper<>(ShowtimeExists.class)
                    .eq(ShowtimeExists::getOrderId, order.getOrderId()));
            if(ObjectUtil.isEmpty(seList)) return null;
            List<String> seatNumbers = seList.stream().map(s -> {
                Seats seat = seatsMapper.selectById(s.getSeatId());
                return seat.getSeatRow() + "排" + seat.getSeatCol() + "座";
            }).toList();
            vo.setSeatNumbers(seatNumbers);
            // 订单时间

            vo.setShowtimeShowDate(seList.get(0).getShowtimeShowDate());
            vo.setShowtimeStartTime(seList.get(0).getShowtimeStartTime());
            // 订单状态
            vo.setOrderStatus(LocalDateTime.of(seList.get(0).getShowtimeShowDate(), seList.get(0).getShowtimeStartTime()).isAfter(LocalDateTime.now()));

            return vo;
        }).filter(Objects::nonNull).toList();

        if(ObjectUtil.isEmpty(list)) return null;
        return list;
    }
}
