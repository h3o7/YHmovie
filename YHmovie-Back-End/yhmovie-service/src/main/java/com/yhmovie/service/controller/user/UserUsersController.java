package com.yhmovie.service.controller.user;

import com.yhmovie.pojo.dto.UsersDto;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.IUsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/users")
public class UserUsersController {
    private final IUsersService usersService;

    @GetMapping("/info/{userId}")
    public Result userInfo(@PathVariable String userId) {
        return Result.success(usersService.getUserInfoById(userId));
    }

    @PutMapping("/update")
    public Result updateUserInfo(@RequestBody UsersDto userDto) {
        return usersService.updateUser(userDto);
    }

}
