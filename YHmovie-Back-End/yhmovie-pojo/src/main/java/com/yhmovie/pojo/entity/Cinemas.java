package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 影院信息表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cinemas implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 影院ID
     */
    @TableId("cinema_id")
    private String cinemaId;

    /**
     * 地点ID
     */
    private String cityId;

    /**
     * 影院名称
     */
    private String cinemaName;

    /**
     * 详细地址
     */
    private String cinemaAddress;

    /**
     * 联系电话
     */
    private String cinemaPhone;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
