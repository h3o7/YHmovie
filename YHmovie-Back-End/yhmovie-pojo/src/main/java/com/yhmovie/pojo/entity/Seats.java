package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 座位信息表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seats implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 座位ID
     */
    @TableId("seat_id")
    private String seatId;

    /**
     * 座位行号
     */
    private Integer seatRow;

    /**
     * 座位列号
     */
    private Integer seatCol;

    /**
     * 座位编号
     */
    private String seatNumber;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
