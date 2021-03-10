package com.java.cloud.training.service.impl;

import java.util.List;
import java.util.Optional;
import com.java.cloud.training.dto.category.CategoryDto;
import com.java.cloud.training.entity.Category;
import com.java.cloud.training.entity.Product;
import com.java.cloud.training.mapper.CategoryMapper;
import com.java.cloud.training.repository.CategoryRepository;
import com.java.cloud.training.service.CategoryService;
import com.java.cloud.training.service.data.CategorySearchData;
import com.java.cloud.training.service.exception.EntityAlreadyExistsException;
import com.java.cloud.training.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import static com.java.cloud.training.specifications.CategorySpecifications.nameContain;
import static com.java.cloud.training.specifications.CategorySpecifications.orderByProductsCount;
import static com.java.cloud.training.service.utils.SortUtils.parseSort;

import static java.util.Objects.nonNull;

@Component
public class CategoryServiceImpl implements CategoryService {

    private static final String DEFAULT_SORT = "products,asc";

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryDto getCategory(String code) {
        Optional<Category> categoryOptional = categoryRepository.findByCode(code);
        if (categoryOptional.isPresent()) {
            return categoryMapper.map(categoryOptional.get(), CategoryDto.class);
        } else {
            throw new EntityNotFoundException("Category with code " + code + " not found");
        }
    }

    @Override
    public List<CategoryDto> getCategories(CategorySearchData categorySearchData) {
        Specification<Category> searchSpecification = nameContain(categorySearchData.getName());
        Sort sort = parseSort(categorySearchData.getSort(), DEFAULT_SORT);

        Sort.Order orderByProducts = sort.getOrderFor("products");
        List<Category> categories;
        if (nonNull(orderByProducts)) {
             searchSpecification = searchSpecification.and(orderByProductsCount(orderByProducts.getDirection()));
            categories = categoryRepository.findAll(searchSpecification);
        } else {
            categories = categoryRepository.findAll(searchSpecification, sort);
        }

        return categoryMapper.mapAsList(categories, CategoryDto.class);
    }

    @Override
    public void createCategory(CategoryDto dto) {
        Optional<Category> categoryOptional = categoryRepository.findByCode(dto.getCode());
        if (categoryOptional.isPresent()) {
            throw new EntityAlreadyExistsException("Category with code " + dto.getCode() + " already exists");
        }
        Category category = categoryMapper.map(dto, Category.class);
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(String code) {
        Optional<Category> categoryOptional = categoryRepository.findByCode(code);
        categoryOptional.ifPresentOrElse(categoryRepository::delete,
                                         () -> { throw new EntityNotFoundException("Category with code " + code + " not found");});
    }

    @Override
    public void updateCategory(CategoryDto dto) {
        categoryRepository.findByCode(dto.getCode())
                .ifPresentOrElse(
                        category -> {
                            categoryMapper.map(dto, category);
                            categoryRepository.save(category);
                        },
                        () -> { throw new EntityNotFoundException("Product with code " + dto.getCode() + " not found");}
                );
    }
}
