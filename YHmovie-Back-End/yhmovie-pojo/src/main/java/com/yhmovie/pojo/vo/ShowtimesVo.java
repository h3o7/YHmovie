package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimesVo {

    private LocalDate showtimeShowDate;
    private LocalTime showtimeStartTime;
    private LocalTime showtimeEndTime;
    private BigDecimal showtimeTicketPrice;

    // 非表中的字段
    private String cinemaName;
    private String moviePosterUrl;
    private String movieName;
    private List<String> movieTypes;
    private Integer movieDuration;

    private Byte movieHallNumber;
    private String movieLanguage;
    /*
    影院 : ACTO梦空间影城（南坪步行街店）
影厅 : 5号激光落地大幕厅
版本 : 国语2D
场次 : 今天 10月5 20:40
票价 : ￥27.9/张
     */

}
