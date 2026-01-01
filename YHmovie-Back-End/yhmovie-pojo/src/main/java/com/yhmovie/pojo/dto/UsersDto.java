package com.yhmovie.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsersDto {
    private String userId;  // 账号

    private String userName;
    // 用户的签名
    private String userSignature;

    private String userPhone;

    private String userGender;

    private LocalDate userBirthDate;

    private String userAvatarUrl;

    // 用于用户登录的字段
    private String userPassword;
    private String confirmPassword;


}
