package com.yhmovie.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yhmovie.pojo.dto.CommentsDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.entity.Comments;
import com.yhmovie.pojo.vo.CommentsVo;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;

/**
 * <p>
 * 评论信息表 服务类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
public interface ICommentsService extends IService<Comments> {

    PageResult<CommentsVo> commentsByMovieId(String movieId, PageRequest pageRequest);

    PageResult<CommentsVo> commentsByCommentId(String commentId, PageRequest pageRequest);

    Result likeComment(String commentId);

    Result addMovieComment(CommentsDto commentsDto);

    Result addReplyComment(CommentsDto commentsDto);
}
