package com.blogapp.service.impl;

import com.blogapp.entity.Comment;
import com.blogapp.entity.CommentLike;
import com.blogapp.entity.Like;
import com.blogapp.entity.User;
import com.blogapp.exception.BlogAPIException;
import com.blogapp.exception.ResourceNotFoundException;
import com.blogapp.repository.CommentLikeRepository;
import com.blogapp.repository.CommentRepository;
import com.blogapp.repository.UserRepository;
import com.blogapp.service.CommentLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    public CommentLikeServiceImpl(CommentLikeRepository commentLikeRepository, UserRepository userRepository,
                                  CommentRepository commentRepository) {
        this.commentLikeRepository = commentLikeRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void likeComment(Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = this.userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("Username could not be found"));

        Comment comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("comments", "id", commentId));

        CommentLike foundCommentLike = this.commentLikeRepository.findByUserAndComment(user, comment)
                .stream().findFirst().orElse(null);

        if (foundCommentLike != null) {
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment can be liked only once");
        }

        CommentLike commentLike = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();

        this.commentLikeRepository.save(commentLike);
    }

    @Override
    public void unlikeComment(Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = this.userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("Username could not be found"));

        Comment comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("comments", "id", commentId));

        CommentLike commentLike = this.commentLikeRepository.findByUserAndComment(user, comment)
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("comments", "id", commentId));

        this.commentLikeRepository.delete(commentLike);
    }
}
