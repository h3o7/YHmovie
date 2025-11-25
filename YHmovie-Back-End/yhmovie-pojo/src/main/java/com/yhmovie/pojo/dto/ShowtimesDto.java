package com.yhmovie.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimesDto {
    private String movieId;
    private String cinemaId;
    private LocalDate showtimeShowDate;
    private LocalTime showtimeStartTime;
    private BigDecimal showtimeTicketPrice; // 精度为2的小数

    // 非showtimes表中的字段
    private String movieHallId;
}
