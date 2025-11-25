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
 * 管理员信息表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admins implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 管理员ID
     */
    @TableId("admin_id")
    private String adminId;



    /**
     * 管理员密码
     */
    private String adminPassword;

    /**
     * 管理员用户名
     */
    private String adminName;

    /**
     * 管理员头像
     */
    private String adminAvatarUrl;

    /**
     * 电话号码
     */
    private String adminPhone;

    /**
     * 管理员状态(1:可用，0:禁用)
     */
    private Byte adminStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
