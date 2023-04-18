package com.blogapp.service.impl;

import com.blogapp.entity.Like;
import com.blogapp.entity.Post;
import com.blogapp.entity.User;
import com.blogapp.exception.BlogAPIException;
import com.blogapp.exception.ResourceNotFoundException;
import com.blogapp.repository.LikeRepository;
import com.blogapp.repository.PostRepository;
import com.blogapp.repository.UserRepository;
import com.blogapp.service.LikeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    public LikeServiceImpl(LikeRepository likeRepository,
                           UserRepository userRepository,
                           PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void likePost(String username, Long postId) {
        User user = this.userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with username " + username + " could not be found"));

        Post post = this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post", "id", postId));

        Like foundLike = this.likeRepository.findByUserAndPost(user, post)
                .orElse(null);

        if (foundLike != null) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Post can be liked only once");
        }

        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();

        this.likeRepository.save(like);
    }

    @Override
    public void unlikePost(String username, Long postId) {
        User user = this.userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with username " + username + " could not be found"));

        Post post = this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post", "id", postId));

        Like like = this.likeRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new ResourceNotFoundException("like", "post id", postId));

        this.likeRepository.delete(like);
    }

    @Override
    public int getLikesCount(Long postId) {
        Post post = this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("post", "id", postId));

        return this.likeRepository.findByPostId(postId).size();
    }
}
