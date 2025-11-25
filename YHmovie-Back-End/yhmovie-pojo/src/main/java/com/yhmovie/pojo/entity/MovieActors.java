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
 * 电影演员关系表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("movie_actors")
public class MovieActors implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 关系ID
     */
    @TableId("movie_actor_id")
    private String movieActorId;

    /**
     * 电影ID
     */
    private String movieId;

    /**
     * 演员ID
     */
    private String actorId;

    /**
     * 角色名称
     */
    private String characterName;

    /**
     * 人物类型(director:导演，actor:演员)
     */
    private String movieRoleType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
