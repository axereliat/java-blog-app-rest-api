package com.blogapp.service;

import com.blogapp.entity.Category;
import com.blogapp.payload.CategoryDto;

import java.util.List;
import java.util.Set;

public interface CategoryService {

    List<Category> getAll();

    Category getById(long id);

    Category create(CategoryDto categoryDto);

    Category update(long id, CategoryDto categoryDto);

    void delete(long id);

    Category findById(long id);
}
