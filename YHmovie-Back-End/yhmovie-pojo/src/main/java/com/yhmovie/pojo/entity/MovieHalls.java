package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 影厅
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("movie_halls")
public class MovieHalls implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 影厅id
     */
    @TableId("movie_hall_id")
    private String movieHallId;

    /**
     * 影厅号
     */
    private Byte movieHallNumber;
}
