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
 * 演员信息表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Actors implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 演员ID
     */
    @TableId("actor_id")
    private String actorId;

    /**
     * 演员姓名
     */
    private String actorName;

    /**
     * 头像URL
     */
    private String actorAvatarUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
