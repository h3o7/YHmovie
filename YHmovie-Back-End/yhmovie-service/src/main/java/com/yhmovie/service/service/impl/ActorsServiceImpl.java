package com.yhmovie.service.service.impl;

import com.yhmovie.pojo.entity.Actors;
import com.yhmovie.service.mapper.ActorsMapper;
import com.yhmovie.service.service.IActorsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 演员信息表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
public class ActorsServiceImpl extends ServiceImpl<ActorsMapper, Actors> implements IActorsService {

}
