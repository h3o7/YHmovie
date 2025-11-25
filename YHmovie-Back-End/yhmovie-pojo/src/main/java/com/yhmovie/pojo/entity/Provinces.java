package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 省份
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Provinces implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 省份id
     */
    @TableId("province_id")
    private String provinceId;

    /**
     * 省份名称
     */
    private String provinceName;

    private Character provincePinyinInitial;
}
