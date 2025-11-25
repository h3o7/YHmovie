package com.yhmovie.service.controller.admin;

import com.yhmovie.pojo.dto.AdminsDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.entity.Admins;
import com.yhmovie.pojo.vo.AdminsVo;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.IAdminsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/admins")
public class AdminAdminsController {
    private final IAdminsService adminsService;

    @GetMapping("/info/{adminId}")
    public Result getAdminInfo(@PathVariable String adminId) {
        AdminsVo adminVo = adminsService.getAdminInfo(adminId);
        if(adminVo == null) return Result.error("管理员不存在");

        return Result.success(adminVo);
    }

    @GetMapping("/list")
    public Result listAdmins(PageRequest pageRequest) {
        PageResult<AdminsVo> list = adminsService.listAdmins(pageRequest);
        return Result.success(list);
    }


    @PutMapping("/update")
    public Result updateAdmin(@RequestBody AdminsDto adminDto) {
        return adminsService.updateAdmin(adminDto);
    }

    @PutMapping("/freeze/{adminId}")
    public Result freeze(@PathVariable String adminId) {
        return adminsService.freeze(adminId);
    }

    @PutMapping("/unfreeze/{adminId}")
    public Result unFreeze(@PathVariable String adminId) {
        return adminsService.unFreeze(adminId);
    }


}
