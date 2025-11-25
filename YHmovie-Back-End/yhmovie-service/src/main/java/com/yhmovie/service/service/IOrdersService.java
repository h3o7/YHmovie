package com.yhmovie.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.entity.Orders;
import com.yhmovie.pojo.vo.OrdersVo;
import com.yhmovie.pojo.vo.Result;

import java.util.List;

/**
 * <p>
 * 电影订单表 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface IOrdersService extends IService<Orders> {

    Result buyTickets(List<String> showtimesIds);

    Result returnTickets(String orderId);

    List<OrdersVo> userOrders();
}
