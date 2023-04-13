package com.blogapp.service.impl;

import com.blogapp.entity.Category;
import com.blogapp.exception.ResourceNotFoundException;
import com.blogapp.payload.CategoryDto;
import com.blogapp.repository.CategoryRepository;
import com.blogapp.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAll() {
        return this.categoryRepository.findAll();
    }

    @Override
    public Category getById(long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    @Override
    public Category create(CategoryDto categoryDto) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .build();

        this.categoryRepository.save(category);

        return category;
    }

    @Override
    public Category update(long id, CategoryDto categoryDto) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setName(categoryDto.getName());
        this.categoryRepository.save(category);

        return category;
    }

    @Override
    public void delete(long id) {
        Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        this.categoryRepository.delete(category);
    }

    @Override
    public Category findById(long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
}
