package com.java.cloud.training.service;

import java.util.List;
import com.java.cloud.training.dto.category.CategoryDto;
import com.java.cloud.training.service.data.CategorySearchData;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryService {

    CategoryDto getCategory(String code);

    List<CategoryDto> getCategories(CategorySearchData categorySearchData);

    @Transactional
    CategoryDto createCategory(CategoryDto dto);

    @Transactional
    void deleteCategory(String code);

    @Transactional
    void updateCategory(CategoryDto dto);
}
