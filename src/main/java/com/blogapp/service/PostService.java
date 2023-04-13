package com.blogapp.service;

import com.blogapp.payload.PostDto;
import com.blogapp.payload.PostResponseDto;

import java.security.Principal;
import java.util.List;

public interface PostService {

    List<PostResponseDto> getAll(Long[] categories);

    PostResponseDto getById(long id);

    PostResponseDto create(PostDto postDto, Principal principal);

    PostResponseDto update(long id, PostDto postDto);

    void delete(long id);
}
