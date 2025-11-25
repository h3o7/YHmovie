package com.yhmovie.service.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yhmovie.common.exception.DBException;
import com.yhmovie.common.util.CurrentHolder;
import com.yhmovie.pojo.dto.CommentsDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.entity.CommentReplies;
import com.yhmovie.pojo.entity.Comments;
import com.yhmovie.pojo.vo.CommentsVo;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.mapper.CommentRepliesMapper;
import com.yhmovie.service.mapper.CommentsMapper;
import com.yhmovie.service.mapper.UsersMapper;
import com.yhmovie.service.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.yhmovie.common.constant.RedisConstant.USER_LIKE_COMMENTS_PREFIX;

/**
 * <p>
 * 评论信息表 服务实现类
 * </p>
 *
 * @author h3o7
 * @since 2025-09-07
 */
@Service
@RequiredArgsConstructor
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements ICommentsService {
    private final CommentsMapper commentsMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final UsersMapper usersMapper;
    private final CommentRepliesMapper commentRepliesMapper;

    @Override
    public PageResult<CommentsVo> commentsByMovieId(String movieId, PageRequest pageRequest) {
        Page<Comments> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        String userId = CurrentHolder.getCurrentId();
        String key = USER_LIKE_COMMENTS_PREFIX + userId;

        // 根据movieId查询评论列表，按热度排序
        Page<Comments> commentsPage = commentsMapper.selectPage(page, new LambdaQueryWrapper<>(Comments.class)
                .eq(Comments::getMovieId, movieId)
                .notInSql(Comments::getCommentId,
                        "select second_comment_id from comment_replies where first_comment_id is not null")
                .orderByDesc(Comments::getCommentLikeNumber));
        List<Comments> comments = commentsPage.getRecords();
        // 将评论列表转换为VO列表
        List<CommentsVo> list = comments.stream().map(co -> {
            CommentsVo commentsVo = new CommentsVo();
            BeanUtils.copyProperties(co, commentsVo);
            commentsVo.setUserName(usersMapper.selectById(co.getUserId()).getUserName());
            commentsVo.setUserAvatarUrl(usersMapper.selectById(co.getUserId()).getUserAvatarUrl());

            Boolean status = stringRedisTemplate.opsForSet().isMember(key, co.getCommentId());
            commentsVo.setLikeStatus(Boolean.TRUE.equals(status));
            return commentsVo;
        }).toList();
        return new PageResult<>((int)commentsPage.getTotal(), list);
    }

    @Override
    public PageResult<CommentsVo> commentsByCommentId(String commentId, PageRequest pageRequest) {
        String userId = CurrentHolder.getCurrentId();
        String key = USER_LIKE_COMMENTS_PREFIX + userId;

        // 根据commentId获取评论id集合
        List<CommentReplies> commentReplies = commentRepliesMapper.selectList(new LambdaQueryWrapper<>(CommentReplies.class)
                .eq(CommentReplies::getFirstCommentId, commentId));
        List<String> commentIds = commentReplies.stream().map(CommentReplies::getSecondCommentId).toList();
        // 根据评论id集合查询评论列表，按热度排序
        Page<Comments> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<Comments> commentsPage = commentsMapper.selectPage(page, new LambdaQueryWrapper<>(Comments.class)
                .in(Comments::getCommentId, commentIds)
                .orderByDesc(Comments::getCommentLikeNumber));
        List<Comments> comments = commentsPage.getRecords();
        // 将评论列表转换为VO列表
        List<CommentsVo> list = comments.stream().map(co -> {
            CommentsVo commentsVo = new CommentsVo();
            BeanUtils.copyProperties(co, commentsVo);
            commentsVo.setUserName(usersMapper.selectById(co.getUserId()).getUserName());
            commentsVo.setUserAvatarUrl(usersMapper.selectById(co.getUserId()).getUserAvatarUrl());
            Boolean status = stringRedisTemplate.opsForSet().isMember(key, co.getCommentId());
            commentsVo.setLikeStatus(Boolean.TRUE.equals(status));
            String repliesUserId = commentRepliesMapper.selectOne(new LambdaQueryWrapper<>(CommentReplies.class)
                    .eq(CommentReplies::getSecondCommentId, co.getCommentId())).getRepliedUserId();
            commentsVo.setRepliedUserName(usersMapper.selectById(repliesUserId).getUserName());
            return commentsVo;
        }).toList();
        return new PageResult<>((int)commentsPage.getTotal(), list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result likeComment(String commentId) {
        String userId = CurrentHolder.getCurrentId();
        String key = USER_LIKE_COMMENTS_PREFIX + userId;
        Boolean member = stringRedisTemplate.opsForSet().isMember(key, commentId);

        // TODO:后续用RabbitMQ异步处理点赞和取消点赞操作
        if(Boolean.TRUE.equals(member)){
            // 说明已点赞，执行取消点赞操作
            stringRedisTemplate.opsForSet().remove(key, commentId);
            int update = commentsMapper.update(null, new LambdaUpdateWrapper<Comments>()
                    .eq(Comments::getCommentId, commentId)
                    .setSql("comment_like_number = comment_like_number - 1"));
            if(update <= 0) throw new DBException("取消点赞失败");

            return Result.success("取消点赞成功");
        } else {
            // 说明未点赞，执行点赞操作
            stringRedisTemplate.opsForSet().add(key, commentId);
            int update = commentsMapper.update(null, new LambdaUpdateWrapper<Comments>()
                    .eq(Comments::getCommentId, commentId)
                    .setSql("comment_like_number = comment_like_number + 1"));
            if(update <= 0) throw new DBException("取消点赞失败");

            return Result.success("点赞成功");
        }

    }

    @Override
    public Result addMovieComment(CommentsDto commentsDto) {
        Comments comment = new Comments();
        comment.setCommentId(IdUtil.simpleUUID());
        comment.setUserId(CurrentHolder.getCurrentId());
        comment.setMovieId(commentsDto.getMovieId());
        comment.setCommentContent(commentsDto.getCommentContent());
        comment.setCommentLikeNumber(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        boolean save = save(comment);
        if(! save) throw new DBException("评论失败");
        return Result.success("评论成功");
    }

    @Override
    public Result addReplyComment(CommentsDto commentsDto) {

        Comments comment = new Comments();
        comment.setCommentId(IdUtil.simpleUUID());
        comment.setUserId(CurrentHolder.getCurrentId());
        comment.setMovieId(commentsDto.getMovieId());
        comment.setCommentContent(commentsDto.getCommentContent());
        comment.setCommentLikeNumber(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());

        CommentReplies commentReplies = new CommentReplies();
        commentReplies.setCommentReplyId(IdUtil.simpleUUID());
        commentReplies.setFirstCommentId(commentsDto.getCommentId());
        commentReplies.setSecondCommentId(comment.getCommentId());
        commentReplies.setRepliedUserId(commentsDto.getRepliedUserId());

        boolean save = save(comment);
        int insert = commentRepliesMapper.insert(commentReplies);
        if(! save || insert <= 0) throw new DBException("回复评论失败");
        return Result.success("回复评论成功");
    }


}
