package com.blogapp.repository;

import com.blogapp.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    List<CommentLike> findByUserAndComment(User user, Comment comment);

    List<CommentLike> findByCommentId(Long commentId);
}
