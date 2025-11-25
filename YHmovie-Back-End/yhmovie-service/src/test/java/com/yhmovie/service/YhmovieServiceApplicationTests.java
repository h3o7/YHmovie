package com.yhmovie.service;

import com.yhmovie.pojo.entity.Movies;
import com.yhmovie.service.mapper.MovieHallsMapper;
import com.yhmovie.service.service.IMoviesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YhmovieServiceApplicationTests {
    @Autowired
    private IMoviesService moviesService;
    @Autowired
    private MovieHallsMapper movieHallsMapper;

    @Test
    void contextLoads() {
        Movies mi1 = moviesService.getById("MI1");
        System.out.println(mi1);
    }

    @Test
    void testMovieHallsMapper() {
        printNum(17);
    }

    void printNum(int number){
        System.out.println(number);
        System.out.println("Hello");

    }

}
