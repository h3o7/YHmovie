package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 影院影厅座位
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("cinema_hall_seats")
public class CinemaHallSeats implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 影院影厅座位id
     */
    @TableId("cinema_hall_seat_id")
    private String cinemaHallSeatId;

    /**
     * 影院id
     */
    private String cinemaId;

    /**
     * 影厅号
     */
    private String movieHallId;

    /**
     * 座位id
     */
    private String seatId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
