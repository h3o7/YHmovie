package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 电影订单表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关系ID
     */
    @TableId("order_id")
    private String orderId;

    /**
     * 用户ID
     */
    private String userId;

    private String movieId;
    private String cinemaId;
    private String movieHallId;



    /**
     * 消费金额
     */
    private BigDecimal orderTotalPrice;



    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
