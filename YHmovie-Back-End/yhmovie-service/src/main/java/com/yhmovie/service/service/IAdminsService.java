package com.yhmovie.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.dto.AdminsDto;
import com.yhmovie.pojo.dto.LoginDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.entity.Admins;
import com.yhmovie.pojo.vo.AdminsVo;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;

/**
 * <p>
 * 管理员信息表 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface IAdminsService extends IService<Admins> {

    Result loginAdmin(LoginDto loginDto);

    AdminsVo getAdminInfo(String adminId);

    Result updateAdmin(AdminsDto adminDto);

    Result freeze(String adminId);

    Result unFreeze(String adminId);

    PageResult<AdminsVo> listAdmins(PageRequest pageRequest);
}
