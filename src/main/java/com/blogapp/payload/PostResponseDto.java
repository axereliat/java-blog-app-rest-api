package com.blogapp.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {

    private Long id;

    private String title;

    private String content;

    private List<CategoryDto> categories;

    private UserDto author;

    private String createdAt;

    private int likes;

    private boolean isLiked;
}
