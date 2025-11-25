package com.yhmovie.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.entity.Cinemas;
import com.yhmovie.pojo.vo.CinemasVo;
import com.yhmovie.pojo.vo.PageResult;

/**
 * <p>
 * 影院信息表 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface ICinemasService extends IService<Cinemas> {

    PageResult<CinemasVo> getCinemasListByCityId(String cityId,Integer pageNum,Integer pageSize);

    PageResult<CinemasVo> getCinemasListByCityIdAndMovieId(String cityId, String movieId, Integer pageNum, Integer pageSize);

    PageResult<CinemasVo> search(String cityId, String cinemaName, Integer pageNum, Integer pageSize);
}
