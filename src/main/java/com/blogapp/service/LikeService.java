package com.blogapp.service;

public interface LikeService {

    void likePost(String username, Long postId);

    void unlikePost(String username, Long postId);

    int getLikesCount(Long postId);
}
