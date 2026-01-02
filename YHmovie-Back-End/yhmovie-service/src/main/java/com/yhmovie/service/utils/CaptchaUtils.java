package com.yhmovie.service.utils;

import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.yhmovie.common.constant.CaptchaConstant.*;
import static com.yhmovie.common.constant.RedisConstant.CAPTCHA_KEY_PREFIX;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "CaptchaUtils")
public class CaptchaUtils {
    private final StringRedisTemplate stringRedisTemplate;

    public String generateCaptcha(HttpServletResponse response) throws IOException {
        // 生成验证码逻辑
        LineCaptcha lineCaptcha = new LineCaptcha(CAPTCHA_WEIGHT,CAPTCHA_HEIGHT,CAPTCHA_CODE_COUNT,CAPTCHA_LINE_COUNT);
        String captchaId = IdUtil.simpleUUID();
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        stringRedisTemplate.opsForValue().set(key, lineCaptcha.getCode(), CAPTCHA_EXPIRE_TIME, TimeUnit.SECONDS);

        // 设置响应头
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 输出图片到响应流
        lineCaptcha.write(response.getOutputStream());
        return captchaId;
    }

    // 验证验证码
    public boolean validateCaptcha(String captchaId, String code) {
        String key = CAPTCHA_KEY_PREFIX + captchaId;
        String storedCode = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(storedCode)) {
            return false;
        }
        // 验证码验证后立即删除（一次性使用）
        stringRedisTemplate.delete(key);
        // 忽略大小写比较
        log.info("前端验证码：{}，存储验证码：{}", code, storedCode);
        return storedCode.equalsIgnoreCase(code);
    }
}
