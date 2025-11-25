package com.yhmovie.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhmovie.common.exception.DBException;
import com.yhmovie.common.exception.InsufficientPermissionException;
import com.yhmovie.common.exception.ResourceNotFoundException;
import com.yhmovie.common.util.CurrentHolder;
import com.yhmovie.common.util.JwtUtils;
import com.yhmovie.pojo.dto.LoginDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.dto.UsersDto;
import com.yhmovie.pojo.entity.Admins;
import com.yhmovie.pojo.entity.Users;
import com.yhmovie.pojo.vo.LoginVo;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.pojo.vo.UsersVo;
import com.yhmovie.service.mapper.AdminsMapper;
import com.yhmovie.service.mapper.UsersMapper;
import com.yhmovie.service.service.IAdminsService;
import com.yhmovie.service.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yhmovie.common.constant.InterceptorConstant.LOGIN_ID;
import static com.yhmovie.common.constant.InterceptorConstant.LOGIN_NAME;
import static com.yhmovie.common.constant.RedisConstant.CAPTCHA_KEY_PREFIX;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
@RequiredArgsConstructor
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    private final StringRedisTemplate redisTemplate;
    private final AdminsMapper adminsMapper;

    @Override
    public UsersVo getUserInfoById(String userId) {
        Users users = baseMapper.selectById(userId);
        if(ObjectUtil.isEmpty(users)) throw new ResourceNotFoundException("未找到对应的用户信息");
        UsersVo usersVo = new UsersVo();
        BeanUtils.copyProperties(users, usersVo);
        usersVo.setUserPhone(users.getUserPhone().substring(0,3) + "******" + users.getUserPhone().substring(9));
        return usersVo;
    }

    @Override
    public Result loginUser(LoginDto loginDto) {
        // 校验验证码
        String key = CAPTCHA_KEY_PREFIX + loginDto.getCaptchaId();
        String captchaCode = redisTemplate.opsForValue().get(key);
        if(ObjectUtil.isEmpty(captchaCode)) return Result.error("验证码已过期");
        if (captchaCode != null && !captchaCode.equals(loginDto.getCaptchaCode())) return Result.error("验证码错误");
        // 校验用户名密码
        Users user = baseMapper.selectById(loginDto.getId());
        if(ObjectUtil.isEmpty(user)) return Result.error("用户不存在");
        if(!user.getUserPassword().equals(loginDto.getPassword())) return Result.error("密码错误");
        if(Byte.valueOf((byte)0).equals(user.getUserStatus())) return Result.error("用户已被禁用");

        Map<String, Object> claims = new HashMap<>();
        claims.put(LOGIN_ID, user.getUserId());
        claims.put(LOGIN_NAME, user.getUserName());
        String token = JwtUtils.generateJwt(claims);
        // 保存token
        LoginVo loginVo = new LoginVo();
        loginVo.setId(user.getUserId());
        loginVo.setName(user.getUserName());
        loginVo.setToken(token);
        return Result.success(loginVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result updateUser(UsersDto userDto) {
        String currentId = CurrentHolder.getCurrentId();
        if(! currentId.equals(userDto.getUserId())) throw new InsufficientPermissionException("权限不足");
        Users user = baseMapper.selectById(userDto.getUserId());
        if(ObjectUtil.isEmpty(user)) return Result.error("用户不存在");

        // 只传递非null值
        if(ObjectUtil.isNotEmpty(userDto.getUserName())) user.setUserName(userDto.getUserName());
        if(ObjectUtil.isNotEmpty(userDto.getUserSignature())) user.setUserSignature(userDto.getUserSignature());
        if(ObjectUtil.isNotEmpty(userDto.getUserPhone())) user.setUserPhone(userDto.getUserPhone());
        if(ObjectUtil.isNotEmpty(userDto.getUserGender())) user.setUserGender(userDto.getUserGender());
        if(ObjectUtil.isNotEmpty(userDto.getUserBirthDate())) user.setUserBirthDate(userDto.getUserBirthDate());
        if(ObjectUtil.isNotEmpty(userDto.getUserAvatarUrl())) user.setUserAvatarUrl(userDto.getUserAvatarUrl());
        int update = baseMapper.updateById(user);
        if(update == 0) return Result.error("用户信息更新失败");
        return Result.success("用户信息更新成功");
    }

    // 管理端相关接口
    @Override
    public Result freeze(String userId) {
        String adminId = CurrentHolder.getCurrentId();
        Admins admin = adminsMapper.selectById(adminId);
        if(ObjectUtil.isEmpty(admin)) throw new InsufficientPermissionException("权限不足");
        Users user = baseMapper.selectById(userId);
        if(ObjectUtil.isEmpty(user)) return Result.error("用户不存在");
        if(user.getUserStatus() == 0) return Result.error("用户已被冻结");
        user.setUserStatus((byte) 0);
        int update = baseMapper.updateById(user);
        if(update == 0) return Result.error("用户冻结失败");
        return Result.success("用户冻结成功");
    }

    @Override
    public Result unFreeze(String userId) {
        String adminId = CurrentHolder.getCurrentId();
        Admins admin = adminsMapper.selectById(adminId);
        if(ObjectUtil.isEmpty(admin)) throw new InsufficientPermissionException("权限不足");
        Users user = baseMapper.selectById(userId);
        if(ObjectUtil.isEmpty(user)) return Result.error("用户不存在");
        if(user.getUserStatus() == 1) return Result.error("用户未被冻结");
        user.setUserStatus((byte) 1);
        int update = baseMapper.updateById(user);
        if(update == 0) return Result.error("用户解冻失败");
        return Result.success("用户解冻成功");
    }

    @Override
    public PageResult<UsersVo> listUsers(PageRequest pageRequest) {
        // id,name,phone,status
        Page<Users> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Users> usersPage = baseMapper.selectPage(page, null);
        List<UsersVo> usersVos = usersPage.getRecords().stream().map(user -> {
            UsersVo usersVo = new UsersVo();
            BeanUtils.copyProperties(user, usersVo);
            usersVo.setUserPhone(user.getUserPhone().substring(0,3) + "******" + user.getUserPhone().substring(9));
            usersVo.setUserStatus(Boolean.TRUE.equals(user.getUserStatus() != null && user.getUserStatus() == 1));
            return usersVo;
        }).toList();
        return new PageResult<>((int) usersPage.getTotal(),usersVos);
    }

    @Override
    public Result deleteUser(String userId) {
        boolean b = removeById(userId);
        if(! b) throw new DBException("用户删除失败");
        return Result.success("用户删除成功");
    }
}
