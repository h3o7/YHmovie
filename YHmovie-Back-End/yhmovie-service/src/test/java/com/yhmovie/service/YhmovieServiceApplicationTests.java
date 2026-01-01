package com.yhmovie.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhmovie.pojo.entity.Movies;
import com.yhmovie.pojo.entity.Types;
import com.yhmovie.pojo.entity.Users;
import com.yhmovie.service.mapper.MovieHallsMapper;
import com.yhmovie.service.mapper.TypesMapper;
import com.yhmovie.service.service.IMoviesService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j(topic = "Tests")
class YhmovieServiceApplicationTests {
    @Autowired
    private IMoviesService moviesService;
    @Autowired
    private MovieHallsMapper movieHallsMapper;
    @Autowired
    private TypesMapper typesMapper;

    @Test
    void contextLoads() {
        Users user = null;
        log.error("返回了类型为{}",ObjectUtil.isEmpty(user));
        if(!ObjectUtil.isEmpty(user)){
            log.error("用户不存在");
        }else{
            log.error("用户存在");
        }
    }

    @Test
    void testMovieHallsMapper() {
        String str = "https://m.baidu.com/sf/vsearch?pd=image_content&word=%E5%9B%BE%E7%89%87%E5%A4%B4%E5%83%8F&tn=vsearch&atn=mediacy&fr=tab&sa=vs_tab&imgtype=12&cs=2997736639,322499180&imgpn=2&imgspn=0&imgis=0,0&imgos=2215894956,1257645527&tt=1&detailfr=relation&di=7581146417410867201&imgcontent=%7B%22subjectJson%22%3A%7B%7D%2C%22queryType%22%3A%22%22%2C%22recParams%22%3A%7B%22cs%22%3A%22360879865%2C1657067670%22%2C%22os%22%3A%222491045633%2C958828514%22%2C%22simid%22%3A%223474538741%2C388127278%22%2C%22bdtype%22%3A0%2C%22is%22%3A%220%2C0%22%7D%7D";
        log.info("str.length = {}", str.length());

    }

    void printNum(int number) {
        System.out.println(number);
        System.out.println("Hello");

    }

}
