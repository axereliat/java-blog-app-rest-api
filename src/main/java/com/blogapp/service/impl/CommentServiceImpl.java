package com.blogapp.service.impl;

import com.blogapp.entity.Comment;
import com.blogapp.entity.Post;
import com.blogapp.entity.User;
import com.blogapp.exception.BlogAPIException;
import com.blogapp.exception.ResourceNotFoundException;
import com.blogapp.payload.CommentDto;
import com.blogapp.payload.CommentRequestDto;
import com.blogapp.payload.CommentResponseDto;
import com.blogapp.payload.UserDto;
import com.blogapp.repository.CommentRepository;
import com.blogapp.repository.PostRepository;
import com.blogapp.repository.UserRepository;
import com.blogapp.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CommentDto> getAll(long postId) {
        List<Comment> comments = this.commentRepository.findAllByPostId(postId);

        List<CommentDto> commentResponseDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponseDtos.add(transformEntityToDto(comment));
        }

        return commentResponseDtos;
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
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        if (!user.getId().equals(comment.getCreatedBy().getId())) {
            throw new ResourceNotFoundException("Comment", "id", commentId);
        }

        if (!user.getId().equals(post.getAuthor().getId())
                && !user.getId().equals(comment.getCreatedBy().getId())
                && !user.isAdmin()) {
            throw new BlogAPIException(HttpStatus.UNAUTHORIZED, "Not authorized");
        }

        this.commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto transformEntityToDto(Comment comment) {
        UserDto userDto = UserDto.builder()
                .id(comment.getCreatedBy().getId())
                .email(comment.getCreatedBy().getEmail())
                .username(comment.getCreatedBy().getUsername())
                .name(comment.getCreatedBy().getName())
                .build();

        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdBy(userDto)
                .createdAt(comment.getCreatedAt().toString())
                .build();
    }
}
