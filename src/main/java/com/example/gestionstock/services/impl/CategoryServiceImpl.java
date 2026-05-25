package com.example.gestionstock.services.impl;

import com.example.gestionstock.entity.Category;
import com.example.gestionstock.repository.CategoryRepository;
import com.example.gestionstock.services.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category addCategory(Category category) {
        Category category1= categoryRepository.save(category);
        return category1;
    }

    @Override
    public Category updateCategory(Category category) {
        if(category.getId()!=null){
            Category category1= categoryRepository.findById(category.getId()).orElse(null);
            if(category1!=null){
                category1.setName(category.getName());
                return categoryRepository.save(category1);
            }
        }
        return null;
    }

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if(category!=null){
            return category;
        }
        return null;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories;
    }
}
