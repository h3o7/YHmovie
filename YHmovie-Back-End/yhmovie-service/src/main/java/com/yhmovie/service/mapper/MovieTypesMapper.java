package com.yhmovie.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhmovie.pojo.entity.MovieTypes;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 电影类型关系表 Mapper 接口
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Mapper
public interface MovieTypesMapper extends BaseMapper<MovieTypes> {

}
