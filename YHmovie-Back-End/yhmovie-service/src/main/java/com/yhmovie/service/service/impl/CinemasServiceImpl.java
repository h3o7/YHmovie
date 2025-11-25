package com.yhmovie.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhmovie.common.exception.ResourceNotFoundException;
import com.yhmovie.pojo.entity.Cinemas;
import com.yhmovie.pojo.entity.Showtimes;
import com.yhmovie.pojo.vo.CinemasVo;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.service.mapper.CinemasMapper;
import com.yhmovie.service.mapper.ShowtimesMapper;
import com.yhmovie.service.service.ICinemasService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 影院信息表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
@RequiredArgsConstructor
public class CinemasServiceImpl extends ServiceImpl<CinemasMapper, Cinemas> implements ICinemasService {
    private final ShowtimesMapper showtimesMapper;

    @Override
    public PageResult<CinemasVo> getCinemasListByCityId(String cityId,Integer pageNum,Integer pageSize) {

        List<Cinemas> cinemasList = baseMapper.selectList(new LambdaQueryWrapper<>(Cinemas.class)
                .eq(Cinemas::getCityId, cityId));

        if(ObjectUtil.isEmpty(cinemasList)){
            throw new ResourceNotFoundException("该城市暂时还没有影院");
        }
        List<CinemasVo> list = cinemasList
                .stream()
                .map(cm -> {
                    CinemasVo cinemasVo = new CinemasVo();
                    BeanUtils.copyProperties(cm, cinemasVo);
                    Showtimes showtimes = showtimesMapper.selectOne(new LambdaQueryWrapper<>(Showtimes.class)
                                    .eq(Showtimes::getCinemaId, cm.getCinemaId())
                                    .orderByAsc(Showtimes::getShowtimeTicketPrice).last("limit 1"));
                    if(ObjectUtil.isEmpty(showtimes)){
                        cinemasVo.setLowestPrice(null);
                    }else {
                        cinemasVo.setLowestPrice(showtimes.getShowtimeTicketPrice());
                    }
                    return cinemasVo;
                })
                .filter(cnv -> cnv.getLowestPrice() != null)
                .toList();
        int total = list.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<CinemasVo> pageList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();

        return new PageResult<>(total, pageList);
    }

    @Override
    public PageResult<CinemasVo> getCinemasListByCityIdAndMovieId(String cityId, String movieId, Integer pageNum, Integer pageSize) {

        List<Cinemas> cinemasList = baseMapper.selectList(new LambdaQueryWrapper<>(Cinemas.class)
                .eq(Cinemas::getCityId, cityId));

        if(ObjectUtil.isEmpty(cinemasList)){
            throw new ResourceNotFoundException("该城市暂时还没有影院");
        }
        List<CinemasVo> list = cinemasList
                .stream()
                .map(cm -> {
                    CinemasVo cinemasVo = new CinemasVo();
                    BeanUtils.copyProperties(cm, cinemasVo);
                    Showtimes showtimes = showtimesMapper.selectOne(new LambdaQueryWrapper<>(Showtimes.class)
                            .eq(Showtimes::getCinemaId, cm.getCinemaId())
                            .eq(Showtimes::getMovieId, movieId)
                            .orderByAsc(Showtimes::getShowtimeTicketPrice).last("limit 1"));
                    if(ObjectUtil.isEmpty(showtimes)){
                        cinemasVo.setLowestPrice(null);
                    }else {
                        cinemasVo.setLowestPrice(showtimes.getShowtimeTicketPrice());
                    }
                    return cinemasVo;
                })
                .filter(cnv -> cnv.getLowestPrice() != null)
                .toList();
        int total = list.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<CinemasVo> pageList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();

        return new PageResult<>(total, pageList);

    }

    @Override
    public PageResult<CinemasVo> search(String cityId, String cinemaName, Integer pageNum, Integer pageSize) {
        List<Cinemas> cinemasList = baseMapper.selectList(new LambdaQueryWrapper<>(Cinemas.class)
                .eq(Cinemas::getCityId, cityId)
                .like(Cinemas::getCinemaName, cinemaName));

        if(ObjectUtil.isEmpty(cinemasList)){
            return new PageResult<>(0, null);
        }
        List<CinemasVo> list = cinemasList
                .stream()
                .map(cm -> {
                    CinemasVo cinemasVo = new CinemasVo();
                    BeanUtils.copyProperties(cm, cinemasVo);
                    Showtimes showtimes = showtimesMapper.selectOne(new LambdaQueryWrapper<>(Showtimes.class)
                            .eq(Showtimes::getCinemaId, cm.getCinemaId())
                            .orderByAsc(Showtimes::getShowtimeTicketPrice).last("limit 1"));
                    if(ObjectUtil.isEmpty(showtimes)){
                        cinemasVo.setLowestPrice(null);
                    }else {
                        cinemasVo.setLowestPrice(showtimes.getShowtimeTicketPrice());
                    }
                    return cinemasVo;
                })
                .filter(cnv -> cnv.getLowestPrice() != null)
                .toList();
        if(ObjectUtil.isEmpty(list))
            return new PageResult<>(0, null);

        int total = list.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<CinemasVo> pageList = fromIndex < total ? list.subList(fromIndex, toIndex) : List.of();

        return new PageResult<>(total, pageList);
    }
}
