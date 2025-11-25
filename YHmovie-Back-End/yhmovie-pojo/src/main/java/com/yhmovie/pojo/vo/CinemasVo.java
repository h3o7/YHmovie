package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CinemasVo {
    private String cinemaId;

    private String cinemaName;

    private String cinemaAddress;

    private String cinemaPhone;

    private BigDecimal lowestPrice;
}
