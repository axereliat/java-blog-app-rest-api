package com.blogapp.payload;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String name;

    private String username;

    private String email;

    private String avatarUrl;
}
