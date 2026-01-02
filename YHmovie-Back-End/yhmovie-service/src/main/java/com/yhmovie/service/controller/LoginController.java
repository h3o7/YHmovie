package com.yhmovie.service.controller;

import com.yhmovie.common.annotation.RateLimit;
import com.yhmovie.pojo.dto.LoginDto;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.IAdminsService;
import com.yhmovie.service.service.IUsersService;
import com.yhmovie.service.utils.CaptchaUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private final IUsersService usersService;
    private final IAdminsService adminsService;
    private final CaptchaUtils captchaUtils;

    @RateLimit(key = "send_code", time = 60, count = 10, msg = "验证码发送太频繁，请1分钟后再试")
    @GetMapping("/captcha")
    public void generateCaptcha(HttpServletResponse response) throws IOException {
        String captchaId = captchaUtils.generateCaptcha(response);
        if (captchaId == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "验证码生成失败");
        } else {
            response.setHeader("captcha-id", captchaId);
            response.setHeader("Access-Control-Expose-Headers", "captcha-id");
        }
    }

    @PostMapping("/users")
    public Result loginUsers(@RequestBody LoginDto loginDto) {
        return usersService.loginUser(loginDto);
    }

    @PostMapping("/admins")
    public Result loginAdmins(@RequestBody LoginDto loginDto) {
        return adminsService.loginAdmin(loginDto);
    }


}
