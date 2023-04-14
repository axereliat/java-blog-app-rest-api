package com.blogapp.controller;

import com.blogapp.payload.CommentDto;
import com.blogapp.payload.CommentRequestDto;
import com.blogapp.payload.CommentResponseDto;
import com.blogapp.payload.PaginationDto;
import com.blogapp.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<PaginationDto> index(@PathVariable Long postId,
                                               @RequestParam(defaultValue = "0") int page) {
        PaginationDto paginationDto = this.commentService.getAll(postId, page);

        return ResponseEntity.ok(paginationDto);
    }

    @PostMapping
    public ResponseEntity<CommentDto> create(@PathVariable Long postId, @RequestBody CommentRequestDto commentDto,
                                             Principal principal) {
        CommentDto commentResponseDto = this.commentService.create(postId, principal, commentDto);

        return ResponseEntity.ok(commentResponseDto);
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity delete(@PathVariable Long id, @PathVariable Long postId, Principal principal) {
        this.commentService.delete(id, postId, principal);

        return ResponseEntity.status(200).build();
    }
}
