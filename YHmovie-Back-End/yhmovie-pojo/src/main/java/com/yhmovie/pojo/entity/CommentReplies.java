package com.yhmovie.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 评论回复表
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment_replies")
public class CommentReplies implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 评论回复表
     */
    @TableId("comment_reply_id")
    private String commentReplyId;

    /**
     * 被回复的评论
     */
    private String firstCommentId;

    /**
     * 回复的评论
     */
    private String secondCommentId;

    /**
     * 被回复的人
     */
    private String repliedUserId;
}
