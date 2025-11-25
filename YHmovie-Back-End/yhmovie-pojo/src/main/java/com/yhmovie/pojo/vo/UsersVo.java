package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersVo {
    private String userId;


    private String userName;
    // 用户的签名
    private String userSignature;

    private String userPhone;   // 134******84（部分显示）

    private String userGender;

    private LocalDate userBirthDate;

    private String userAvatarUrl;

    // 用户状态 ture-正常 false-冻结 用于管理端展示的信息(id,name,phone，状态)
    private boolean userStatus;


}
