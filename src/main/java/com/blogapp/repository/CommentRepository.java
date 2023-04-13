package com.blogapp.repository;

import com.blogapp.entity.Category;
import com.blogapp.entity.Comment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(@Qualifier Long postId);
}
