package com.blogapp.controller;

import com.blogapp.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/posts")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{id}/like")
    public ResponseEntity likePost(@PathVariable Long id, Principal principal) {
        this.likeService.likePost(principal.getName(), id);

        return ResponseEntity.status(200).build();
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity unlikePost(@PathVariable Long id, Principal principal) {
        this.likeService.unlikePost(principal.getName(), id);

        return ResponseEntity.status(200).build();
    }
}
