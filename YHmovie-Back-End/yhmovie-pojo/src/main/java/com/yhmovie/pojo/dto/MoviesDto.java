package com.yhmovie.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class MoviesDto {
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

    private LocalDate movieEndDate;

    private List<String> movieTypes;
}
