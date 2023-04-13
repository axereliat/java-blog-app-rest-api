package com.blogapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"))
    private Set<Category> categories;

    @OneToMany(mappedBy = "post")
    private Set<Comment> comments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void createdAt() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void updatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
