package com.blogapp.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {

    private Long id;

    private String content;

    private String author;

    private String createdAt;
}
