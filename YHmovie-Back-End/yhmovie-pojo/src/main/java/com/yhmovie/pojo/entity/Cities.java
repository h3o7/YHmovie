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
 * 城市表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cities implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 城市ID
     */
    @TableId("city_id")
    private String cityId;

    /**
     * 城市所属省份id
     */
    private String provinceId;

    /**
     * 城市名称
     */
    private String cityName;
    private Character cityPinyinInitial;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
