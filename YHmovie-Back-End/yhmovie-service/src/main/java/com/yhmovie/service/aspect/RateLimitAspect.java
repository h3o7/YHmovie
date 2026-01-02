package com.yhmovie.service.aspect;

import com.yhmovie.common.annotation.RateLimit;
import com.yhmovie.common.exception.RateLimitException;
import com.yhmovie.common.util.IpUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j(topic = "RateLimitAspect")
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Before("@annotation(rateLimit)")
    public void doBefore(JoinPoint point, RateLimit rateLimit) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;

        HttpServletRequest request = attributes.getRequest();

        // 1. 使用工具类获取真实IP
        String ip = IpUtils.getIpAddr(request);

        // 2. 获取方法名或自定义key
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        String keyName = rateLimit.key();

        // 如果注解没有指定key，则使用方法名
        if (keyName == null || keyName.isEmpty()) {
            keyName = method.getName();
        }

        // 3. 构建 Redis Key: "rate_limit:sendCode:192.168.1.10"
        String redisKey = "rate_limit:" + keyName + ":" + ip;

        // 4. 执行限流逻辑
        long time = rateLimit.time();
        int count = rateLimit.count();

        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        // 如果是第一次访问，设置过期时间
        if (currentCount != null && currentCount == 1) {
            redisTemplate.expire(redisKey, time, TimeUnit.SECONDS);
        }

        // 5. 打印日志方便调试
        log.debug("IP: {} 访问接口: {}, 当前次数: {}, 限制次数: {}", ip, keyName, currentCount, count);

        if (currentCount != null && currentCount > count) {
            log.error("IP: {} 访问接口: {} 超过限制次数: {}", ip, keyName, count);
            throw new RateLimitException(rateLimit.msg());
        }
    }
}