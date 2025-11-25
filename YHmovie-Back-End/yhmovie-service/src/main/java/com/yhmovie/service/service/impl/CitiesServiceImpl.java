package com.yhmovie.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhmovie.pojo.entity.Cities;
import com.yhmovie.pojo.entity.Provinces;
import com.yhmovie.pojo.vo.CitiesVo;
import com.yhmovie.service.mapper.CitiesMapper;
import com.yhmovie.service.mapper.ProvincesMapper;
import com.yhmovie.service.service.ICitiesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 城市表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
@RequiredArgsConstructor
public class CitiesServiceImpl extends ServiceImpl<CitiesMapper, Cities> implements ICitiesService {
    private final ProvincesMapper provincesMapper;
    @Override
    public List<CitiesVo> getCitiesListByProvinceId(String provinceId) {
        List<Cities> list = baseMapper.selectList(new LambdaQueryWrapper<Cities>()
                .eq(Cities::getProvinceId, provinceId)
                .orderByAsc(Cities::getCityPinyinInitial));
        List<CitiesVo> citiesVoList = new ArrayList<>();
        for (Cities cities : list) {
            CitiesVo citiesVO = new CitiesVo();
            BeanUtils.copyProperties(cities, citiesVO);
            String provinceName = provincesMapper.selectOne(new LambdaQueryWrapper<Provinces>()
                    .eq(Provinces::getProvinceId, cities.getProvinceId()))
                    .getProvinceName();
            citiesVO.setProvinceName(provinceName);
            citiesVoList.add(citiesVO);
        }
        return citiesVoList;
    }

    @Override
    public List<CitiesVo> searchCitiesByCityName(String cityName) {
        List<Cities> list = baseMapper.selectList(new LambdaQueryWrapper<Cities>()
                .like(Cities::getCityName, cityName)
                .orderByAsc(Cities::getCityPinyinInitial));
        List<CitiesVo> citiesVoList = new ArrayList<>();
        for (Cities cities : list) {
            CitiesVo citiesVO = new CitiesVo();
            BeanUtils.copyProperties(cities, citiesVO);
            String provinceName = provincesMapper.selectOne(new LambdaQueryWrapper<Provinces>()
                    .eq(Provinces::getProvinceId, cities.getProvinceId()))
                    .getProvinceName();
            citiesVO.setProvinceName(provinceName);
            citiesVoList.add(citiesVO);
        }
        return citiesVoList;
    }
}
