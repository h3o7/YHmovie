package com.yhmovie.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static com.yhmovie.common.constant.InterceptorConstant.JWT_EXPIRATION;
import static com.yhmovie.common.constant.InterceptorConstant.JWT_SECRET_KEY;

public class JwtUtils {
    public static String generateJwt(Map<String,Object> claims){
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .compact();
    }

    public static Claims parseJwt(String token){
        return Jwts.parser()
                .setSigningKey(JWT_SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

}
