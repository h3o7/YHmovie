package com.yhmovie.common.interceptor;

import com.yhmovie.common.util.CurrentHolder;
import com.yhmovie.common.util.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.yhmovie.common.constant.InterceptorConstant.*;

@Slf4j
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO: 通过token验证相关请求的合法性
        String url = request.getRequestURL().toString();

        //2. 判断请求url中是否包含login，如果包含，说明是登录操作，放行。
        if(url.contains(LOGIN)) return true; // 登录请求
        //3. 从请求头中获取token
        String jwt = request.getHeader(AUTHORIZE_TOKEN).substring(7);
//        System.err.println(url + " : " + request.getRequestURI() + ":" + jwt);
//        log.info("请求地址: {}", request.getRequestURI());

        if(!StringUtils.hasLength(jwt)){ //jwt为空
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            log.info("TokenInterceptor: token为空");
            log.info(request.getRequestURI());
            return false;
        }

        //5. 解析token，如果解析失败，返回错误结果（未登录）。
        try {
            Claims claims = JwtUtils.parseJwt(jwt);
            if(claims == null) { //claims为空
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            String id = claims.get(LOGIN_ID).toString();
            CurrentHolder.setCurrentId(id);
            // 刷新token过期时间

            return true;

        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

    }



    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        CurrentHolder.clear();
    }

}
