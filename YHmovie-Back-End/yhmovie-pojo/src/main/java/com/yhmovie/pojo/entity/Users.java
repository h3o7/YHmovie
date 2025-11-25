package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户信息表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId("user_id")
    private String userId;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户简介
     */
    private String userSignature;

    /**
     * 手机号码
     */
    private String userPhone;

    /**
     * 性别
     */
    private String userGender;

    /**
     * 出生日期
     */
    private LocalDate userBirthDate;

    /**
     * 头像URL
     */
    private String userAvatarUrl;

    /**
     * 最后登录时间
     */
    private LocalDateTime userLastLoginTime;

    /**
     * 用户状态（1：可用，0:禁用）
     */
    private Byte userStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
