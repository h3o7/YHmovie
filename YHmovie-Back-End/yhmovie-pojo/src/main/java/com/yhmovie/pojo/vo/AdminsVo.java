package com.yhmovie.pojo.vo;

import lombok.Data;

@Data
public class AdminsVo {
    private String adminId;

    private String adminName;

    private String adminPhone;   // 134******84（部分显示）

    private String adminAvatarUrl;

    // 管理员状态 ture-正常 false-冻结 用于管理端展示的信息(id,name,phone)
    private boolean adminStatus;
}
