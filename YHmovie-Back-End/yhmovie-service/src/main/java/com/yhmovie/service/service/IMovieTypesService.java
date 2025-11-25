package com.yhmovie.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.entity.MovieTypes;

import java.util.List;

/**
 * <p>
 * 电影类型关系表 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface IMovieTypesService extends IService<MovieTypes> {

    boolean addOrUpdateMovieType(List<String> movieTypes,String movieId);

}
