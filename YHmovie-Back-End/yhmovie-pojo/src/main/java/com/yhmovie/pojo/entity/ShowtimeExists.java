package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("showtime_exists")
public class ShowtimeExists implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 座位状态表id
     */
    @TableId("showtime_exist_id")
    private String showtimeExitId;

    /**
     * 场次id
     */
    private String showtimeId;


    private String orderId;
    private String seatId;

    private LocalDate showtimeShowDate;
    private LocalTime showtimeStartTime;
}
