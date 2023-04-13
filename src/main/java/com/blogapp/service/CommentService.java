package com.blogapp.service;

import com.blogapp.entity.Comment;
import com.blogapp.payload.CommentDto;
import com.blogapp.payload.CommentRequestDto;

import java.security.Principal;
import java.util.List;

public interface CommentService {

    List<CommentDto> getAll(long postId);

    CommentDto create(long postId, Principal principal, CommentRequestDto commentDto);

    void delete(long commentId, long postId, Principal principal);

    CommentDto transformEntityToDto(Comment comment);
}
