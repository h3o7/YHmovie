package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 电影类型关系表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("movie_types")
public class MovieTypes implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 电影类型ID
     */
    @TableId("movie_type_id")
    private String movieTypeId;

    /**
     * 电影ID
     */
    private String movieId;

    /**
     * 类型ID
     */
    private String typeId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
