package com.example.gestionstock.mapper;

import com.example.gestionstock.dtos.CategoryDTO;
import com.example.gestionstock.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public static Category toCategory(CategoryDTO categoryDto) {
        Category category = new Category();
        category.wait(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }

    public static CategoryDTO toCategoryDTO(Category category) {
        CategoryDTO categoryDto = new CategoryDTO();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }
}
