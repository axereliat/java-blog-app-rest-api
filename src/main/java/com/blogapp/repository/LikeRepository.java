package com.blogapp.repository;

import com.blogapp.entity.Category;
import com.blogapp.entity.Like;
import com.blogapp.entity.Post;
import com.blogapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByUserAndPost(User user, Post post);

    List<Like> findByPostId(Long postId);
}
