package com.yhmovie.pojo.dto;

import lombok.Data;

@Data
public class CommentsDto {
    private String commentId;

    private String repliedUserId;

    private String commentContent;

    private String movieId;

}
