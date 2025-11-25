package com.yhmovie.service.controller.admin;

import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.pojo.vo.UsersVo;
import com.yhmovie.service.service.IUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUsersController {
    private final IUsersService usersService;

    @GetMapping("/list")
    public Result list(PageRequest pageRequest) {
        PageResult<UsersVo> pageResult = usersService.listUsers(pageRequest);
        return Result.success(pageResult);
    }

    // 冻结用户
    @PutMapping("/freeze/{userId}" )
    public Result freeze(@PathVariable("userId") String userId) {
        return usersService.freeze(userId);
    }

    // 解冻用户
    @PutMapping("/unfreeze/{userId}" )
    public Result unfreeze(@PathVariable("userId") String userId) {
        return usersService.unFreeze(userId);
    }

    @DeleteMapping("/{userId}" )
    public Result deleteUser(@PathVariable("userId") String userId) {
        return usersService.deleteUser(userId);
    }


}
