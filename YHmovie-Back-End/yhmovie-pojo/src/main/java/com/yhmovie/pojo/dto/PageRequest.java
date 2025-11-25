package com.yhmovie.pojo.dto;

import lombok.Data;

@Data
public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
