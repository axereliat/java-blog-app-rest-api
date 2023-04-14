package com.blogapp.service.impl;

import com.blogapp.entity.Category;
import com.blogapp.entity.Comment;
import com.blogapp.entity.Post;
import com.blogapp.entity.User;
import com.blogapp.exception.ResourceNotFoundException;
import com.blogapp.payload.CommentDto;
import com.blogapp.payload.PostDto;
import com.blogapp.payload.PostResponseDto;
import com.blogapp.repository.PostRepository;
import com.blogapp.repository.UserRepository;
import com.blogapp.service.CategoryService;
import com.blogapp.service.CommentService;
import com.blogapp.service.PostService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CategoryService categoryService;

    private final CommentService commentService;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository,
                           CategoryService categoryService, CommentService commentService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }

    @Override
    public List<PostResponseDto> getAll(Long[] categories) {
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        for (Post post : this.postRepository.findAll().stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .collect(Collectors.toList())) {
            List<Long> categoryIds = post.getCategories().stream().map(Category::getId).toList();
            List<Long> categoriesInReq;
            if (categories == null) {
                categoriesInReq = new ArrayList<>();
            } else {
                categoriesInReq = List.of(categories);
            }

            if (categoriesInReq.size() > 0 && !categoriesMatch(categoryIds, categoriesInReq)) {
                continue;
            }

            PostResponseDto postResponseDto = this.transformEntityToDto(post);
            postResponseDtos.add(postResponseDto);
        }

        return postResponseDtos;
    }

    @Override
    public PostResponseDto getById(long id) {
        Post post = this.postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        return this.transformEntityToDto(post);
    }

    @Override
    public PostResponseDto create(PostDto postDto, Principal principal) {
        User user = this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username could not be found"));

        Set<Category> categories = this.getCategories(postDto.getCategoryIds());

        Post post = Post.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .categories(categories)
                .author(user)
                .build();

        this.postRepository.save(post);

        return transformEntityToDto(post);
    }

    @Override
    public PostResponseDto update(long id, PostDto postDto) {
        Post post = this.postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());

        Set<Category> categories = getCategories(postDto.getCategoryIds());
        post.setCategories(categories);

        this.postRepository.save(post);

        return this.transformEntityToDto(post);
    }

    @Override
    public void delete(long id) {
        this.postRepository.deleteById(id);
    }

    private Set<Category> getCategories(List<Integer> categoryIds) {
        Set<Category> categories = new HashSet<>();

        for (Integer categoryId : categoryIds) {
            categories.add(this.categoryService.findById(categoryId));
        }

        return categories;
    }

    private PostResponseDto transformEntityToDto(Post post) {
        PostResponseDto postDto = new PostResponseDto();
        postDto.setId(post.getId());
        postDto.setContent(post.getContent());
        postDto.setTitle(post.getTitle());
        postDto.setAuthor(post.getAuthor().getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        postDto.setCreatedAt(post.getCreatedAt().format(formatter));

        List<String> categoryNames = post.getCategories().stream().map(Category::getName).collect(Collectors.toList());
        postDto.setCategories(categoryNames);

        return postDto;
    }

    private boolean categoriesMatch(List<Long> arrOne, List<Long> arrTwo) {
        List<Long> catsOne = new ArrayList<>(arrOne.size() > arrTwo.size() ? arrOne : arrTwo);
        List<Long> catsTwo = new ArrayList<>(arrOne.size() > arrTwo.size() ? arrTwo : arrOne);

        boolean fl = false;
        for (Long aLong : catsOne) {
            for (Long bLong : catsTwo) {
                if (Objects.equals(aLong, bLong)) {
                    fl = true;
                    break;
                }
            }
            if (fl) {
                break;
            }
        }

        return fl;
    }
}
