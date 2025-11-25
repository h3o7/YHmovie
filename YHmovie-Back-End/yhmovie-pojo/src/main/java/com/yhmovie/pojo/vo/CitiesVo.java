package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitiesVo {
    private String cityId;
    private String provinceName;
    private String cityName;
    private Character cityPinyinInitial;
}
