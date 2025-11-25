package com.yhmovie.service.service.impl;

import com.yhmovie.pojo.entity.MovieActors;
import com.yhmovie.service.mapper.MovieActorsMapper;
import com.yhmovie.service.service.IMovieActorsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 电影演员关系表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
public class MovieActorsServiceImpl extends ServiceImpl<MovieActorsMapper, MovieActors> implements IMovieActorsService {

}
