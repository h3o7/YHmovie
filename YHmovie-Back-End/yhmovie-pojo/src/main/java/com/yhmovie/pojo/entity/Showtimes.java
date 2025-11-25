package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 场次信息表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtimes implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 场次ID
     */
    @TableId("showtime_id")
    private String showtimeId;

    /**
     * 电影ID
     */
    private String movieId;
    private String cinemaId;
    private String movieHallId;
    private String cinemaHallSeatId;
    private LocalDate showtimeShowDate;

    /**
     * 场次开始时间
     */
    private LocalTime showtimeStartTime;

    /**
     * 场次结束时间
     */
    private LocalTime showtimeEndTime;

    /**
     * 票价
     */
    private BigDecimal showtimeTicketPrice;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
