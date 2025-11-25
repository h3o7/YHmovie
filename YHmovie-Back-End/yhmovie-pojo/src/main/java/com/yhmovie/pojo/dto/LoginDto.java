package com.yhmovie.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String id;
    private String password;
    private String captchaId;
    private String captchaCode;
}
