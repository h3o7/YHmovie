package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimesOrderVo {
    // 用于该接口（Get "user/showtimes/list-all/"）
    private String showtimeId;
    private String cinemaHallSeatId;
    private BigDecimal showtimeTicketPrice;

    // 非表中字段
    private String seatNumber;  // 几排几座
    private Boolean soldStatus; // 用于标注该座位是否被售出  true:已售出  false:未售出
}
