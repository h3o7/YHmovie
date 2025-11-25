package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoviesListVo {
    private String movieId;
    private String moviePosterUrl;
    private String movieName;
    private BigDecimal movieRating;
    private BigDecimal movieBoxOffice;
}
