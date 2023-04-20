package com.blogapp.service.impl;

import com.blogapp.entity.Comment;
import com.blogapp.entity.CommentLike;
import com.blogapp.entity.Post;
import com.blogapp.entity.User;
import com.blogapp.exception.BlogAPIException;
import com.blogapp.exception.ResourceNotFoundException;
import com.blogapp.payload.*;
import com.blogapp.repository.CommentLikeRepository;
import com.blogapp.repository.CommentRepository;
import com.blogapp.repository.PostRepository;
import com.blogapp.repository.UserRepository;
import com.blogapp.service.CommentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    public static final int PAGE_SIZE = 5;

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CommentLikeRepository commentLikeRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
                              UserRepository userRepository, CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    @Override
    public PaginationDto getAll(long postId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Pageable nextPageable = PageRequest.of(page + 1, PAGE_SIZE);

        List<Comment> comments = this.commentRepository.findAllByPostId(postId, pageable);
        List<Comment> nextComments = this.commentRepository.findAllByPostId(postId, nextPageable);

        List<CommentDto> commentResponseDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDtos.add(transformEntityToDto(comment));
        }

        return PaginationDto.builder()
                .items(commentResponseDtos)
                .page(page)
                .hasMore(!nextComments.isEmpty())
                .build();
    }

    @Override
    public CommentDto create(long postId, Principal principal, CommentRequestDto commentDto) {

        Post post = this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment comment = Comment.builder()
                .content(commentDto.getContent())
                .build();

        comment.setPost(post);

        User user = this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username could not be found"));
        comment.setCreatedBy(user);

        Comment newComment = this.commentRepository.save(comment);

        this.postRepository.save(post);

        CommentDto commentResponseDto = transformEntityToDto(newComment);

        return commentResponseDto;
    }

    @Override
    public void delete(long commentId, long postId, Principal principal) {
        User user = this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username could not be found"));

        Post post = this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        Comment comment = this.commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!user.getId().equals(comment.getCreatedBy().getId())) {
            throw new ResourceNotFoundException("Comment", "id", commentId);
        }

        if (!user.getId().equals(post.getAuthor().getId())
                && !user.getId().equals(comment.getCreatedBy().getId())
                && !user.isAdmin()) {
            throw new BlogAPIException(HttpStatus.UNAUTHORIZED, "Not authorized");
        }

        List<CommentLike> byUserAndComment = this.commentLikeRepository.findByUserAndComment(user, comment);
        this.commentLikeRepository.deleteById(byUserAndComment.get(0).getId());
        this.commentRepository.deleteById(commentId);
    }


    @Override
    public void deleteCommentsFromPost(long postId) {
        Post post = this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        this.commentRepository.deleteAll(post.getComments());
    }

    @Override
    public CommentDto transformEntityToDto(Comment comment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = this.userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("Username could not be found"));

        UserDto userDto = UserDto.builder()
                .id(comment.getCreatedBy().getId())
                .email(comment.getCreatedBy().getEmail())
                .username(comment.getCreatedBy().getUsername())
                .name(comment.getCreatedBy().getName())
                .build();

        int likesCount = this.commentLikeRepository.findByCommentId(comment.getId()).size();

        boolean isLiked = this.commentLikeRepository.findByUserAndComment(user, comment).stream().findFirst()
                .isPresent();

        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdBy(userDto)
                .createdAt(comment.getCreatedAt().toString())
                .likes(likesCount)
                .isLiked(isLiked)
                .build();
    }
}
