package com.blogapp.payload;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;

    private String content;

    private UserDto createdBy;

    private String createdAt;

    private int likes;

    private boolean isLiked;
}
