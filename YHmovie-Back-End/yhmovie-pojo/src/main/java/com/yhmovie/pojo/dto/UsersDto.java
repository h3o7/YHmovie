package com.yhmovie.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsersDto {
    private String userId;

    private String userName;
    // 用户的签名
    private String userSignature;

    private String userPhone;

    private String userGender;

    private LocalDate userBirthDate;

    private String userAvatarUrl;
}
