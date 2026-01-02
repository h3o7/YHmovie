package com.yhmovie.common.annotation;



import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流键的前缀（用于区分不同业务）
     */
    String key() default "limit";

    /**
     * 限流时间（单位：秒），默认60秒
     */
    int time() default 60;

    /**
     * 限流次数，默认50次
     */
    int count() default 50;

    /**
     * 限流提示信息
     */
    String msg() default "访问过于频繁，请稍后再试";
}
