package com.yhmovie.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhmovie.common.exception.DBException;
import com.yhmovie.pojo.entity.MovieTypes;
import com.yhmovie.pojo.entity.Types;
import com.yhmovie.service.mapper.MovieTypesMapper;
import com.yhmovie.service.mapper.TypesMapper;
import com.yhmovie.service.service.IMovieTypesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 电影类型关系表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
@RequiredArgsConstructor
public class MovieTypesServiceImpl extends ServiceImpl<MovieTypesMapper, MovieTypes> implements IMovieTypesService {
    private final TypesMapper typesMapper;

    @Override
    public boolean addOrUpdateMovieType(List<String> movieTypes, String movieId) {
        // 先删除原有的类型
        boolean remove = remove(new LambdaQueryWrapper<>(MovieTypes.class).eq(MovieTypes::getMovieId, movieId));
        if(! remove) throw new DBException("删除原有电影类型失败");
        // 再添加新的类型
        movieTypes.forEach(typeName -> {
            String typeId = typesMapper.selectOne(new LambdaQueryWrapper<>(Types.class)
                    .eq(Types::getTypeName, typeName)).getTypeId();
            if (typeId == null) throw new DBException("类型不存在");
            MovieTypes movieType = new MovieTypes();
            movieType.setMovieId(movieId);
            movieType.setTypeId(typeId);
            movieType.setCreateTime(LocalDateTime.now());
            movieType.setUpdateTime(LocalDateTime.now());
            boolean saveMovieType = save(movieType);
            if (!saveMovieType) throw new DBException("新增电影类型关系失败");
        });
        return true;
    }
}
