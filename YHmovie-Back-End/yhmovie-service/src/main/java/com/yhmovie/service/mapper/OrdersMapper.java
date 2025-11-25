package com.yhmovie.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhmovie.pojo.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 电影订单表 Mapper 接口
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}
