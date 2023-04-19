package com.blogapp.controller;

import com.blogapp.service.CommentLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    public CommentLikeController(CommentLikeService commentLikeService) {
        this.commentLikeService = commentLikeService;
    }

    @PostMapping("/{id}/like")
    public ResponseEntity like(@PathVariable Long id) {
        this.commentLikeService.likeComment(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity unlike(@PathVariable Long id) {
        this.commentLikeService.unlikeComment(id);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
