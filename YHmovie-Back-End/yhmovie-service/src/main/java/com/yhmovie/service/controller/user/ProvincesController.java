package com.yhmovie.service.controller.user;

import com.yhmovie.pojo.entity.Provinces;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.IProvincesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/provinces")
public class ProvincesController {
    private final IProvincesService provincesService;

    @GetMapping("/list")
    public Result provincesList() {
        List<Provinces> list = provincesService.getProvincesList();
        return Result.success(list);
    }
}
