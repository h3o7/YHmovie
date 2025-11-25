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
 * 类型信息表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Types implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类型ID
     */
    @TableId("type_id")
    private String typeId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型描述
     */
    private String typeDescription;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
