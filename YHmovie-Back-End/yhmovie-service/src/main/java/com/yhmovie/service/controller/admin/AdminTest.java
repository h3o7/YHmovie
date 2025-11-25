package com.yhmovie.service.controller.admin;

import com.yhmovie.common.exception.DBException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminTest {
    @GetMapping("/hello")
    public String hello() {
    try {
        int i = 1 / 0;
    }catch(RuntimeException e){
        throw new DBException("资源未找到异常");
    }

        return "Hello, Admin!";
    }
}
