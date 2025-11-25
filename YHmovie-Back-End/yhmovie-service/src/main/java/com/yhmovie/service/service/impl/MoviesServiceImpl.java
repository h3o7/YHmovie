package com.yhmovie.service.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhmovie.common.exception.ResourceNotFoundException;
import com.yhmovie.pojo.dto.MoviesDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.entity.*;
import com.yhmovie.pojo.vo.*;
import com.yhmovie.service.mapper.*;
import com.yhmovie.service.service.IMovieTypesService;
import com.yhmovie.service.service.IMoviesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 电影信息表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
@RequiredArgsConstructor
public class MoviesServiceImpl extends ServiceImpl<MoviesMapper, Movies> implements IMoviesService {
    private final IMovieTypesService movieTypesService;
    private final TypesMapper typesMapper;
    private final MovieTypesMapper movieTypesMapper;
    private final MovieActorsMapper movieActorsMapper;
    private final ActorsMapper actorsMapper;
    private final ShowtimesMapper showtimesMapper;
    private final MoviesMapper moviesMapper;

    @Override
    public PageResult<MoviesListVo> showingMoviesList(Integer pageNum, Integer pageSize) {
        Page<Movies> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Movies> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(Movies::getMovieReleaseDate, LocalDateTime.now())
                .gt(Movies::getMovieEndDate, LocalDateTime.now());
        Page<Movies> moviesPage = baseMapper.selectPage(page, wrapper);

        List<MoviesListVo> listVos = moviesPage.getRecords()
                .stream()
                .map(movie -> {
                    MoviesListVo moviesListVo = new MoviesListVo();
                    BeanUtils.copyProperties(movie, moviesListVo);
                    return moviesListVo;
                })
                .toList();
        return new PageResult<>((int) moviesPage.getTotal(), listVos);
    }

    @Override
    public PageResult<MoviesListVo> comingMoviesList(Integer pageNum, Integer pageSize) {
        Page<Movies> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Movies> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Movies::getMovieReleaseDate, LocalDateTime.now());
        Page<Movies> moviesPage = baseMapper.selectPage(page, wrapper);

        List<MoviesListVo> listVos = moviesPage.getRecords()
                .stream()
                .map(movie -> {
                    MoviesListVo moviesListVo = new MoviesListVo();
                    BeanUtils.copyProperties(movie, moviesListVo);
                    return moviesListVo;
                })
                .toList();
        return new PageResult<>((int) moviesPage.getTotal(), listVos);
    }

    @Override
    public PageResult<MoviesListVo> moviesListByCinemaId(String cinemaId) {
        List<String> MIlist = showtimesMapper.selectList(new LambdaQueryWrapper<>(Showtimes.class)
                .eq(Showtimes::getCinemaId, cinemaId)).stream().map(Showtimes::getMovieId).distinct().toList();
        if (ObjectUtil.isEmpty(MIlist))
            throw new ResourceNotFoundException("该影院暂无排片电影");
        List<MoviesListVo> result = MIlist.stream().map(mi -> {
            MoviesListVo moviesListVo = new MoviesListVo();
            BeanUtils.copyProperties(moviesMapper.selectById(mi), moviesListVo);
            return moviesListVo;
        }).toList();
        return new PageResult<>(result.size(), result);
    }

    @Override
    public MoviesVo movieDetail(String movieId) {
        Movies movie = baseMapper.selectById(movieId);
        if (ObjectUtil.isEmpty(movie))
            throw new ResourceNotFoundException("电影不存在");

        MoviesVo moviesVo = new MoviesVo();
        BeanUtils.copyProperties(movie, moviesVo);
        List<MovieTypes> movieTypes = movieTypesMapper.selectList(new LambdaQueryWrapper<>(MovieTypes.class)
                .eq(MovieTypes::getMovieId, movieId));
        List<String> typesNameList = movieTypes.stream().map(mt -> {
            Types type = typesMapper.selectById(mt.getTypeId());
            return type.getTypeName();
        }).toList();
        moviesVo.setMovieTypes(typesNameList);
        return moviesVo;
    }

    @Override
    public PageResult<MovieActorsVo> movieActors(String movieId) {
        List<MovieActors> list = movieActorsMapper.selectList(new LambdaQueryWrapper<>(MovieActors.class)
                .eq(MovieActors::getMovieId, movieId));
        if (ObjectUtil.isEmpty(list)) {
            throw new ResourceNotFoundException("暂无演员信息");
        }
        List<MovieActorsVo> actorList = list.stream()
                .map(ma -> {
                    MovieActorsVo movieActorsVo = new MovieActorsVo();
                    Actors actor = actorsMapper.selectById(ma.getActorId());
                    BeanUtils.copyProperties(actor, movieActorsVo);
                    String movieRoleType = ma.getMovieRoleType().equals("director") ? "导演" : "演员";
                    movieActorsVo.setMovieRoleType(movieRoleType);
                    movieActorsVo.setCharacterName(ma.getCharacterName());
                    return movieActorsVo;
                }).toList();
        return new PageResult<>(actorList.size(), actorList);
    }

    @Override
    public PageResult<MoviesListVo> boxOfficeRanking(PageRequest pageRequest) {
        Page<Movies> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Movies> moviesPage = baseMapper.selectPage(page, new LambdaQueryWrapper<>(Movies.class)
                .orderByDesc(Movies::getMovieBoxOffice));

        List<MoviesListVo> list = moviesPage.getRecords()
                .stream().map(m -> {
                    MoviesListVo moviesListVo = new MoviesListVo();
                    BeanUtils.copyProperties(m, moviesListVo);
                    return moviesListVo;
                }).toList();
        return new PageResult<>((int) moviesPage.getTotal(), list);
    }

    @Override
    public PageResult<MoviesListVo> ratingRanking(PageRequest pageRequest) {
        Page<Movies> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Movies> moviesPage = baseMapper.selectPage(page, new LambdaQueryWrapper<>(Movies.class)
                .orderByDesc(Movies::getMovieRating));

        List<MoviesListVo> list = moviesPage.getRecords()
                .stream().map(m -> {
                    MoviesListVo moviesListVo = new MoviesListVo();
                    BeanUtils.copyProperties(m, moviesListVo);
                    return moviesListVo;
                }).toList();
        return new PageResult<>((int) moviesPage.getTotal(), list);
    }

    @Override
    public PageResult<MoviesListVo> searchMovies(String movieName, Integer pageNum, Integer pageSize) {
        // TODO:根据关键词搜索电影（模糊匹配）
        Page<Movies> page = new Page<>(pageNum, pageSize);
        Page<Movies> moviesPage = baseMapper.selectPage(page, new LambdaQueryWrapper<>(Movies.class)
                .like(Movies::getMovieName, movieName));
        if (ObjectUtil.isEmpty(moviesPage)) throw new ResourceNotFoundException("未找到相关电影");
        List<MoviesListVo> list = moviesPage.getRecords().stream().map(m -> {
            MoviesListVo moviesListVo = new MoviesListVo();
            BeanUtils.copyProperties(m, moviesListVo);
            return moviesListVo;
        }).toList();
        return new PageResult<>((int) moviesPage.getTotal(), list);
    }

    // 管理端的相关接口的实现
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result addMovie(MoviesDto movieDto) {
        Movies movie = new Movies();
        BeanUtils.copyProperties(movieDto, movie);
        if (movie.getMovieId() == null) movie.setMovieId(IdUtil.simpleUUID());
        movie.setCreateTime(LocalDateTime.now());
        movie.setUpdateTime(LocalDateTime.now());
        int insert = baseMapper.insert(movie);
        boolean saveMT = movieTypesService.addOrUpdateMovieType(movieDto.getMovieTypes(), movie.getMovieId());
        if (!saveMT || insert != 1)
            return Result.error("新增电影失败");
        return Result.success("新增电影成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateMovie(MoviesDto movieDto) {
        Movies movie = getById(movieDto.getMovieId());
        if (ObjectUtil.isEmpty(movie))
            throw new ResourceNotFoundException("电影不存在");
        BeanUtils.copyProperties(movieDto, movie);
        movie.setUpdateTime(LocalDateTime.now());
        int update = baseMapper.updateById(movie);
        boolean saveMT = movieTypesService.addOrUpdateMovieType(movieDto.getMovieTypes(), movie.getMovieId());
        if (!saveMT || update != 1)
            return Result.error("更新电影失败");
        return Result.success("更新电影成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteMovie(String movieId) {
        boolean remove = removeById(movieId);
        int deleteMT = movieTypesMapper.delete(new LambdaQueryWrapper<>(MovieTypes.class)
                .eq(MovieTypes::getMovieId, movieId));
        int deleteMA = movieActorsMapper.delete(new LambdaQueryWrapper<>(MovieActors.class)
                .eq(MovieActors::getMovieId, movieId));
        if (!remove || deleteMT < 0 || deleteMA < 0)
            throw new ResourceNotFoundException("删除电影失败");
        return Result.success("删除电影成功");
    }


}
