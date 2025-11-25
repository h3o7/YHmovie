package com.yhmovie.service.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhmovie.common.exception.InsufficientPermissionException;
import com.yhmovie.common.util.CurrentHolder;
import com.yhmovie.common.util.JwtUtils;
import com.yhmovie.pojo.dto.AdminsDto;
import com.yhmovie.pojo.dto.LoginDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.entity.Admins;
import com.yhmovie.pojo.vo.AdminsVo;
import com.yhmovie.pojo.vo.LoginVo;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.mapper.AdminsMapper;
import com.yhmovie.service.service.IAdminsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yhmovie.common.constant.InterceptorConstant.LOGIN_ID;
import static com.yhmovie.common.constant.InterceptorConstant.LOGIN_NAME;
import static com.yhmovie.common.constant.RedisConstant.CAPTCHA_KEY_PREFIX;

/**
 * <p>
 * 管理员信息表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
@RequiredArgsConstructor
public class AdminsServiceImpl extends ServiceImpl<AdminsMapper, Admins> implements IAdminsService {
    private final StringRedisTemplate redisTemplate;

    @Override
    public Result loginAdmin(LoginDto loginDto) {
        // 校验验证码
        String key = CAPTCHA_KEY_PREFIX + loginDto.getCaptchaId();
        String captchaCode = redisTemplate.opsForValue().get(key);
        if(ObjectUtil.isEmpty(captchaCode)) return Result.error("验证码已过期");
        if (captchaCode != null && !captchaCode.equals(loginDto.getCaptchaCode())) return Result.error("验证码错误");
        // 校验管理员密码
        Admins admin = baseMapper.selectById(loginDto.getId());
        if(ObjectUtil.isEmpty(admin)) return Result.error("管理员账号不存在");
        if(!admin.getAdminPassword().equals(loginDto.getPassword())) return Result.error("密码错误");
        if(Byte.valueOf((byte)0).equals(admin.getAdminStatus())) return Result.error("用户已被禁用");

        Map<String, Object> claims = new HashMap<>();
        claims.put(LOGIN_ID,admin.getAdminId());
        claims.put(LOGIN_NAME, admin.getAdminName());
        String token = JwtUtils.generateJwt(claims);
        LoginVo loginVo = new LoginVo();
        loginVo.setId(admin.getAdminId());
        loginVo.setName(admin.getAdminName());
        loginVo.setToken(token);
        return Result.success(loginVo);
    }

    // 管理端相关接口的实现
    @Override
    public AdminsVo getAdminInfo(String adminId) {
        Admins admins = baseMapper.selectById(adminId);
        AdminsVo adminsVo = new AdminsVo();
        if(ObjectUtil.isEmpty(admins)) return null;
        BeanUtil.copyProperties(admins, adminsVo);
        String phone = admins.getAdminPhone();
        adminsVo.setAdminPhone(phone.substring(0, 3) + "******" + phone.substring(9));
        return adminsVo;
    }

    @Override
    public Result updateAdmin(AdminsDto adminDto) {
        Admins admin = getById(adminDto.getAdminId());
        if(ObjectUtil.isEmpty(admin)) return Result.error("管理员不存在");
        BeanUtil.copyProperties(adminDto, admin);
        boolean update = updateById(admin);
        if(!update) return Result.error("更新失败");
        return Result.success("更新成功");
    }

    @Override
    public Result freeze(String adminId) {
        String currentId = CurrentHolder.getCurrentId();
        Admins currentAdmin = baseMapper.selectById(currentId);
        if(ObjectUtil.isEmpty(currentAdmin)) throw new InsufficientPermissionException("权限不足");

        Admins admin = baseMapper.selectById(adminId);
        if(ObjectUtil.isEmpty(admin)) return Result.error("管理员不存在");
        if(Byte.valueOf((byte)0).equals(admin.getAdminStatus())) return Result.error("管理员已被冻结");
        admin.setAdminStatus((byte)0);
        boolean update = updateById(admin);
        if(!update) return Result.error("冻结失败");
        return Result.success("冻结成功");
    }

    @Override
    public Result unFreeze(String adminId) {
        String currentId = CurrentHolder.getCurrentId();
        Admins currentAdmin = baseMapper.selectById(currentId);
        if(ObjectUtil.isEmpty(currentAdmin)) throw new InsufficientPermissionException("权限不足");
        Admins admin = baseMapper.selectById(adminId);
        if(ObjectUtil.isEmpty(admin)) return Result.error("管理员不存在");
        if(Byte.valueOf((byte)1).equals(admin.getAdminStatus())) return Result.error("管理员未被冻结");
        admin.setAdminStatus((byte)1);
        boolean update = updateById(admin);
        if(!update) return Result.error("解冻失败");
        return Result.success("解冻成功");
    }

    @Override
    public PageResult<AdminsVo> listAdmins(PageRequest pageRequest) {
        Page<Admins> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Admins> adminsPage = baseMapper.selectPage(page, null);
        List<AdminsVo> list = adminsPage.getRecords().stream()
                .map(admin -> {
                    AdminsVo adminsVo = getAdminInfo(admin.getAdminId());
                    adminsVo.setAdminStatus(Boolean.TRUE.equals(admin.getAdminStatus() != null && admin.getAdminStatus() == 1));
                    return adminsVo;
                }).toList();
        return new PageResult<>((int) adminsPage.getTotal(), list);
    }
}
