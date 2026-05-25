package com.example.gestionstock.services;

import com.example.gestionstock.entity.Category;

import java.util.List;

public interface CategoryService {
    // Interface pour les services de gestion des catégories
    public Category addCategory(Category category);
    public Category updateCategory(Category category);
    public void deleteCategory(Long categoryId);
    public Category getCategoryById(Long categoryId);
    public List<Category> getAllCategories();
}
