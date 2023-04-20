package com.blogapp.payload;

import com.blogapp.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
public class PostDto {

    @NotBlank
    @Size(min = 3, max = 30)
    private String title;

    @NotBlank
    private String content;

    @Size(min = 1, message = "At least 1 category must be selected")
    private List<Integer> categoryIds;
}
