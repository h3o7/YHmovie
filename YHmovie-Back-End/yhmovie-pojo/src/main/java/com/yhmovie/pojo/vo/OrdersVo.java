package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersVo {
    private String orderId;

    private String movieName;
    private String moviePosterUrl;
    private String cinemaName;
    private Integer movieHallNumber;

    private List<String> seatNumbers;
    private BigDecimal orderTotalPrice;

    private LocalDateTime createTime;
    private LocalDate showtimeShowDate;
    private LocalTime showtimeStartTime;
    private boolean orderStatus;    // true:未观看（可以退票); false:已观看（不可退票）
}
