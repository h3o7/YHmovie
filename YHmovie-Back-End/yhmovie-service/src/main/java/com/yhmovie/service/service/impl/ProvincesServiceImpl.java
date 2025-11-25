package com.yhmovie.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yhmovie.pojo.entity.Provinces;
import com.yhmovie.service.mapper.ProvincesMapper;
import com.yhmovie.service.service.IProvincesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 省份 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
public class ProvincesServiceImpl extends ServiceImpl<ProvincesMapper, Provinces> implements IProvincesService {

    @Override
    public List<Provinces> getProvincesList() {
        return baseMapper.selectList(new LambdaQueryWrapper<Provinces>()
                .orderByAsc(Provinces::getProvincePinyinInitial));

    }
}
