package com.yhmovie.service.controller.user;

import com.aliyuncs.exceptions.ClientException;
import com.yhmovie.pojo.dto.UsersDto;
import com.yhmovie.pojo.entity.Users;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.IUsersService;
import com.yhmovie.service.utils.AliyunOSSOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/users")
@Slf4j(topic = "UserUsersController")
public class UserUsersController {
    private final IUsersService usersService;
    private final AliyunOSSOperator aliyunOSSOperator;

    @GetMapping("/info/{userId}")
    public Result userInfo(@PathVariable String userId) {
        return Result.success(usersService.getUserInfoById(userId));
    }

    @PutMapping("/update")
    public Result updateUserInfo(@RequestBody UsersDto userDto) throws ClientException {
        return usersService.updateUser(userDto);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UsersDto userDto) throws ClientException {
        log.error("访问注册接口userDto: {}", userDto);
        Result result = usersService.register(userDto);
        if(result.getCode() != 200){
            String userAvatarUrl = userDto.getUserAvatarUrl();
            if(userAvatarUrl != null && !userAvatarUrl.isEmpty()){
                log.debug("userAvatarUrl: {}", userAvatarUrl);
                aliyunOSSOperator.deleteFile(userAvatarUrl);
            }
            return result;
        }
        return result;
    }

}
