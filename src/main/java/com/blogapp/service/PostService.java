package com.blogapp.service;

import com.blogapp.payload.PaginationDto;
import com.blogapp.payload.PostDto;
import com.blogapp.payload.PostResponseDto;

import java.security.Principal;

public interface PostService {

    PaginationDto getAll(Long[] categories, String keyword, int page);

    PostResponseDto getById(long id);

    PostResponseDto create(PostDto postDto, Principal principal);

    PostResponseDto update(long id, PostDto postDto);

    void delete(long id);
}
