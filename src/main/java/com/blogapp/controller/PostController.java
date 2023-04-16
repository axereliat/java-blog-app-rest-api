package com.blogapp.controller;

import com.blogapp.payload.PaginationDto;
import com.blogapp.payload.PostDto;
import com.blogapp.payload.PostResponseDto;
import com.blogapp.service.CommentService;
import com.blogapp.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<PaginationDto> index(@RequestParam(required = false) Long[] categories,
                                               @RequestParam(required = false, defaultValue = "0") Integer page) {
        PaginationDto paginationDto = this.postService.getAll(categories, page);

        return ResponseEntity.ok(paginationDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> details(@PathVariable Long id) {
        PostResponseDto post = this.postService.getById(id);

        return ResponseEntity.ok(post);
    }

    @PostMapping
    public ResponseEntity<PostResponseDto> create(@RequestBody PostDto postDto, Principal principal) {
        PostResponseDto post = this.postService.create(postDto, principal);

        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> update(@PathVariable Long id, @RequestBody PostDto postDto) {
        PostResponseDto post = this.postService.update(id, postDto);

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        this.commentService.deleteCommentsFromPost(id);
        this.postService.delete(id);

        return ResponseEntity.status(200).build();
    }
}
