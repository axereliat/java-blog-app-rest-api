package com.blogapp.payload;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDto {

    private String content;
}
