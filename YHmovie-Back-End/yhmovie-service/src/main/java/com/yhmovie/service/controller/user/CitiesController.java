package com.yhmovie.service.controller.user;

import com.yhmovie.pojo.vo.CitiesVo;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.ICitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/cities")
public class CitiesController {
    private final ICitiesService citiesService;

    // 根据省份ID获取城市列表
    @GetMapping("/list/{provinceId}")
    public Result citiesListByProvinceId(@PathVariable String provinceId) {
        List<CitiesVo> list = citiesService.getCitiesListByProvinceId(provinceId);
        return Result.success(list);
    }

    @GetMapping("/search")
    public Result searchCities(@RequestParam String cityName) {
        List<CitiesVo> list = citiesService.searchCitiesByCityName(cityName);
        return Result.success(list);
    }

}
