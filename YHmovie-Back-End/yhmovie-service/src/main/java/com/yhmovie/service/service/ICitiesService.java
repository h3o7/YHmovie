package com.yhmovie.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.entity.Cities;
import com.yhmovie.pojo.vo.CitiesVo;

import java.util.List;

/**
 * <p>
 * 城市表 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface ICitiesService extends IService<Cities> {

    List<CitiesVo> getCitiesListByProvinceId(String provinceId);

    List<CitiesVo> searchCitiesByCityName(String cityName);
}
