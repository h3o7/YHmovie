package com.yhmovie.service.service;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.dto.LoginDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.dto.UsersDto;
import com.yhmovie.pojo.entity.Users;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.pojo.vo.UsersVo;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface IUsersService extends IService<Users> {

    UsersVo getUserInfoById(String userId);

    Result loginUser(LoginDto loginDto);

    Result updateUser(UsersDto userDto) throws ClientException;

    Result freeze(String userId);

    Result unFreeze(String userId);

    PageResult<UsersVo> listUsers(PageRequest pageRequest);

    Result deleteUser(String userId);

    Result register(UsersDto userDto);

}
