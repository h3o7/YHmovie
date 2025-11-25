package com.yhmovie.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.yhmovie.service.mapper")
@ComponentScan("com.yhmovie")
public class YhmovieServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(YhmovieServiceApplication.class, args);
    }

}
