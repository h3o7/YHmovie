package com.yhmovie.service.controller.user;

import com.yhmovie.pojo.dto.CommentsDto;
import com.yhmovie.pojo.dto.PageRequest;
import com.yhmovie.pojo.vo.CommentsVo;
import com.yhmovie.pojo.vo.PageResult;
import com.yhmovie.pojo.vo.Result;
import com.yhmovie.service.service.ICommentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/comments")
public class UserCommentsController {

    private final ICommentsService commentsService;


    @GetMapping("/list/movie/{movieId}" )
    public Result movieCommentsList(@PathVariable String movieId, @ModelAttribute PageRequest pageRequest) {
        PageResult<CommentsVo> pageResult = commentsService.commentsByMovieId(movieId, pageRequest);
        return Result.success(pageResult);
    }

    @GetMapping("/list/reply/{commentId}" )
    public Result replyCommentList(@PathVariable String commentId, @ModelAttribute PageRequest pageRequest) {
        PageResult<CommentsVo> pageResult = commentsService.commentsByCommentId(commentId, pageRequest);
        return Result.success(pageResult);
    }

    @PutMapping("/like/{commentId}" )
    public Result likeComment(@PathVariable String commentId){
        return commentsService.likeComment(commentId);
    }

    @PostMapping("/add/movie")
    public Result addComment(@RequestBody CommentsDto commentsDto){
        return commentsService.addMovieComment(commentsDto);
    }

    @PostMapping("/add/reply")
    public Result addReply(@RequestBody CommentsDto commentsDto){
        return commentsService.addReplyComment(commentsDto);
    }
}
