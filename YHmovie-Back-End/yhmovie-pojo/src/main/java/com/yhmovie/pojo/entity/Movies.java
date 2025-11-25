package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 电影信息表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movies implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 电影ID
     */
    @TableId("movie_id")
    private String movieId;

    /**
     * 海报图片URL
     */
    private String moviePosterUrl;

    /**
     * 电影名称
     */
    private String movieName;

    /**
     * 时长(分钟)
     */
    private Integer movieDuration;

    /**
     * 评分
     */
    private BigDecimal movieRating;

    /**
     * 电影描述
     */
    private String movieDescription;

    /**
     * 制片国家
     */
    private String movieCountry;

    /**
     * 语言
     */
    private String movieLanguage;

    /**
     * 电影票房
     */
    private BigDecimal movieBoxOffice;

    /**
     * 上映日期
     */
    private LocalDate movieReleaseDate;

    /**
     * 下映日期
     */
    private LocalDate movieEndDate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
