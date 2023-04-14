package com.blogapp.service;

import com.blogapp.entity.Comment;
import com.blogapp.payload.CommentDto;
import com.blogapp.payload.CommentRequestDto;
import com.blogapp.payload.PaginationDto;

import java.security.Principal;

public interface CommentService {

    PaginationDto getAll(long postId, int page);

    CommentDto create(long postId, Principal principal, CommentRequestDto commentDto);

    void delete(long commentId, long postId, Principal principal);

    CommentDto transformEntityToDto(Comment comment);
}
