package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimesListVo {
    private String movieHallId;
    private LocalDate showtimeShowDate;
    private LocalTime showtimeStartTime;
    private LocalTime showtimeEndTime;
    private BigDecimal leastPrice;

    // 非表中的字段
    private Byte movieHallNumber;
    private String movieLanguage;
}
