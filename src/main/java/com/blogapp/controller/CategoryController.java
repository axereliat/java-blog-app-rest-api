package com.blogapp.controller;

import com.blogapp.entity.Category;
import com.blogapp.payload.CategoryDto;
import com.blogapp.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> index() {
        List<Category> categories = this.categoryService.getAll();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> details(@PathVariable Long id) {
        Category category = this.categoryService.getById(id);

        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody CategoryDto categoryDto) {
        Category category = this.categoryService.create(categoryDto);

        return ResponseEntity.ok(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        Category category = this.categoryService.update(id, categoryDto);

        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        this.categoryService.delete(id);

        return ResponseEntity.status(200).build();
    }
}
