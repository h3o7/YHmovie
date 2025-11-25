package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 国家表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Countrys implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 国家id
     */
    @TableId("country_id")
    private String countryId;

    /**
     * 国家名称
     */
    private String countryName;
}
