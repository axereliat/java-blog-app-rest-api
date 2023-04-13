package com.blogapp.payload;

import com.blogapp.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private String title;

    private String content;

    private List<Integer> categoryIds;
}
