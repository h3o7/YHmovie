package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoviesVo {
    private String movieId;

    private String moviePosterUrl;

    private String movieName;

    private Integer movieDuration;

    private BigDecimal movieRating;

    private String movieDescription;

    private String movieCountry;

    private String movieLanguage;

    private BigDecimal movieBoxOffice;

    private LocalDate movieReleaseDate;

    private List<String> movieTypes;




}
