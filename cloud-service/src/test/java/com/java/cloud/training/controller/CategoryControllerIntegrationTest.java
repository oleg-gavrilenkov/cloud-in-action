package com.java.cloud.training.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JavaType;
import com.java.cloud.training.dto.category.CategoryDto;
import com.java.cloud.training.dto.category.UpdateCategoryDto;
import com.java.cloud.training.entity.Category;
import com.java.cloud.training.entity.Product;
import com.java.cloud.training.repository.CategoryRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class CategoryControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Order(1)
    void shouldReturnAllCategoriesSortedByProductsCountAsc() throws Exception {
        MvcResult response = mockMvc.perform(get("/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryDto.class);
        List<CategoryDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), type);

        assertThat(result.size()).isEqualTo(generatedCategories.size());
        checkThatCategoriesOrderedByProductCountsAsc(result);
    }

    @Test
    @Order(2)
    void shouldReturnCategoryByCode() throws Exception {
        Category category = generatedCategories.get(0);
        MvcResult response = mockMvc.perform(get("/categories/{categoryCode}", category.getCode())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        CategoryDto result = objectMapper.readValue(response.getResponse().getContentAsString(), CategoryDto.class);

        assertThat(result.getCode()).isEqualTo(category.getCode());
        assertThat(result.getName()).isEqualTo(category.getName());
        assertThat(result.getProducts().size()).isEqualTo(category.getProducts().size());
    }

    @Test
    @Order(3)
    void shouldReturn404Status_whenCategoryByCodeNotFound() throws Exception {
        mockMvc.perform(get("/categories/{categoryCode}", "emptyCategoryCode")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void shouldFilterCategoriesByName() throws Exception {
        Category category = generatedCategories.get(0);

        MvcResult response = mockMvc.perform(get("/categories")
                .param("name", category.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, CategoryDto.class);
        List<CategoryDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), type);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getCode()).isEqualTo(category.getCode());
        assertThat(result.get(0).getName()).isEqualTo(category.getName());
        assertThat(result.get(0).getProducts().size()).isEqualTo(category.getProducts().size());
    }

    @Test
    @Order(5)
    void shouldUpdateCategoryByCode() throws Exception {
        Category category = generatedCategories.get(0);
        UpdateCategoryDto expectedDto = modifyAndConvertToDto(category);
        mockMvc.perform(put("/categories/{categoryCode}", category.getCode())
                .content(objectMapper.writeValueAsString(expectedDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Category updatedCategory = categoryRepository
                .findByCode(category.getCode()).get();
        assertThat(updatedCategory.getName()).isEqualTo(expectedDto.getName());
        assertThat(updatedCategory.getProducts().size()).isEqualTo(expectedDto.getProducts().size());
    }

    @Test
    @Order(6)
    void shouldReturn404Status_whenTryToUpdateNoExistingCategory() throws Exception {
        Category category = generatedCategories.get(1);
        UpdateCategoryDto expectedDto = modifyAndConvertToDto(category);
        mockMvc.perform(put("/categories/{categoryCode}", "emptyCategoryCode")
                .content(objectMapper.writeValueAsString(expectedDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    void shouldReturn404Status_whenTryToDeleteNoExistingCategory() throws Exception {
        mockMvc.perform(delete("/categories/{categoryCode}", "emptyCategoryCode"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    void shouldDeleteCategoryByCode() throws Exception {
        String categoryCode = generatedCategories.get(0).getCode();
        mockMvc.perform(delete("/categories/{categoryCode}", categoryCode))
                .andExpect(status().isOk());
        generatedCategories.remove(0);

        Optional<Category> category = categoryRepository.findByCode(categoryCode);

        assertThat(category.isPresent()).isFalse();
    }

    @Test
    @Order(9)
    void shouldCreateCategory() throws Exception {
        CategoryDto dto = testDataGenerator.generateRandomData(CategoryDto.class);
        dto.setProducts(generatedProducts.stream().map(Product::getCode).collect(Collectors.toList()));
        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        Optional<Category> createdCategory = categoryRepository.findByCode(dto.getCode());
        assertThat(createdCategory.isPresent()).isTrue();
    }

    @Test
    @Order(10)
    void shouldReturn409Status_whenTryToCreateCategoryWithExistingCode() throws Exception {
        CategoryDto dto = testDataGenerator.generateRandomData(CategoryDto.class);
        dto.setProducts(generatedProducts.stream().map(Product::getCode).collect(Collectors.toList()));
        dto.setCode(generatedCategories.get(0).getCode());

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());

    }

    private UpdateCategoryDto modifyAndConvertToDto(Category category) {
        UpdateCategoryDto dto = new UpdateCategoryDto();
        dto.setName(category.getName() + randomInt(10));
        dto.setProducts(category.getProducts().stream()
                .limit(1).map(Product::getCode)
                .collect(Collectors.toList()));
        return dto;
    }

    private void checkThatCategoriesOrderedByProductCountsAsc(List<CategoryDto> result) {
        if (result.size() <= 1) {
            return;
        }

        int previousCategoryProductsCount;
        int currentCategoryProductsCount;
        for (int index = 1; index < result.size(); index++) {
            previousCategoryProductsCount = result.get(index - 1).getProducts().size();
            currentCategoryProductsCount = result.get(index).getProducts().size();

            assertThat(previousCategoryProductsCount).isLessThanOrEqualTo(currentCategoryProductsCount);
        }
    }

}
