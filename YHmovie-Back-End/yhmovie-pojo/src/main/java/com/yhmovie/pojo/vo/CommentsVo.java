package com.yhmovie.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentsVo {
    private String commentId;

    private String userId;

    private String commentContent;

    private Integer commentLikeNumber;

    private LocalDateTime createTime;

    // 非表中字段
    private String userName;
    private String userAvatarUrl;
    private boolean likeStatus;     // true表示已点赞，false表示未点赞

    private String repliedUserName;
}
