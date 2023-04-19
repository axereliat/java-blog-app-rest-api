package com.blogapp.service;

public interface CommentLikeService {

    void likeComment(Long commentId);

    void unlikeComment(Long commentId);
}
