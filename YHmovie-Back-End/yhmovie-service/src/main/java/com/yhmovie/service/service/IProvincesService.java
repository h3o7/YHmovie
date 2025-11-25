package com.yhmovie.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.entity.Provinces;

import java.util.List;

/**
 * <p>
 * 省份 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface IProvincesService extends IService<Provinces> {

    List<Provinces> getProvincesList();
}
