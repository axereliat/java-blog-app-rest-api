package com.blogapp.payload;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationDto {

    private Object items;

    private Integer page;

    private boolean hasMore;
}
