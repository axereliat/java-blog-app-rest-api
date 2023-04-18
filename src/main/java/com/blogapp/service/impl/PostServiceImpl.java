package com.blogapp.service.impl;

import com.blogapp.entity.Category;
import com.blogapp.entity.Like;
import com.blogapp.entity.Post;
import com.blogapp.entity.User;
import com.blogapp.exception.ResourceNotFoundException;
import com.blogapp.payload.*;
import com.blogapp.repository.LikeRepository;
import com.blogapp.repository.PostRepository;
import com.blogapp.repository.UserRepository;
import com.blogapp.service.CategoryService;
import com.blogapp.service.CommentService;
import com.blogapp.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final int PAGE_SIZE = 5;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final CategoryService categoryService;

    private final CommentService commentService;

    private final LikeRepository likeRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository,
                           CategoryService categoryService, CommentService commentService, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.likeRepository = likeRepository;
    }

    @Override
    public PaginationDto getAll(Long[] categories, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Pageable nextPageable = PageRequest.of(page + 1, PAGE_SIZE);

        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        List<Post> nextList = this.postRepository.findAll(nextPageable).stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .collect(Collectors.toList());

        for (Post post : this.postRepository.findAll(pageable).stream()
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

        return PaginationDto.builder()
                .items(postResponseDtos)
                .page(page)
                .hasMore(!nextList.isEmpty())
                .build();
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

        List<Like> likes = this.likeRepository.findByPostId(post.getId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = this.userRepository.findByEmail(currentPrincipalName)
                .orElseThrow(() -> new UsernameNotFoundException("Username could not be found"));

        boolean liked = likes.stream().anyMatch(l -> l.getPost().getId().equals(post.getId())
        && l.getUser().getId().equals(user.getId()));

        postDto.setLikes(likes.size());
        postDto.setLiked(liked);

        UserDto userDto = UserDto.builder()
                .id(post.getAuthor().getId())
                .email(post.getAuthor().getEmail())
                .name(post.getAuthor().getName())
                .username(post.getAuthor().getUsername())
                .build();

        postDto.setAuthor(userDto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        postDto.setCreatedAt(post.getCreatedAt().format(formatter));

        List<CategoryDto> categoryDtos = new ArrayList<>();
        for (Category category : post.getCategories()) {
            categoryDtos.add(new CategoryDto(category.getId(), category.getName()));
        }

        postDto.setCategories(categoryDtos);

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
