package com.example.gestionstock.controller;

import com.example.gestionstock.dtos.CategoryDTO;
import com.example.gestionstock.entity.Category;
import com.example.gestionstock.services.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute Category categoryDTO) {
        categoryService.addCategory(categoryDTO);
        return "redirect:/products";
    }

}
