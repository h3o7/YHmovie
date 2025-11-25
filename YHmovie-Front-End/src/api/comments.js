import request from "@/utils/request";

export const commentsByMovieId = (movieId, pageNum, pageSize) => request.get(`/user/comments/list/movie/${movieId}?pageNum=${pageNum}&pageSize=${pageSize}`);

export const commentsByCommentId = (commentId,pageNum,pageSize) => request.get(`/user/comments/list/reply/${commentId}?pageNum=${pageNum}&pageSize=${pageSize}`);

export const likeComment = (commentId) => request.put(`/user/comments/like/${commentId}`);

export const addMovieComment = (commentsDto) => request.post('/user/comments/add/movie', commentsDto);

export const addReplyComment = (commentsDto) => request.post('/user/comments/add/reply', commentsDto);