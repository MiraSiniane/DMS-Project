package com.service.document.controller;

import com.service.document.entity.Category;
import com.service.document.repository.CategoryRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
  private final CategoryRepository repo;
  public CategoryController(CategoryRepository repo) { this.repo = repo; }

  @PostMapping
  public ResponseEntity<Category> create(@RequestBody Category c) {
    return ResponseEntity.ok(repo.save(c));
  }

  @GetMapping
  public ResponseEntity<List<Category>> list() {
    return ResponseEntity.ok(repo.findAll());
  }
}
