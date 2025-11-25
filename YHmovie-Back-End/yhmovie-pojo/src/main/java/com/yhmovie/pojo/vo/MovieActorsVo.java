package com.yhmovie.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieActorsVo {
    private String actorId;
    private String actorName;
    private String characterName;
    private String actorAvatarUrl;
    private String movieRoleType;
}
