package com.java.cloud.training.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JavaType;
import com.java.cloud.training.dto.product.ProductDto;
import com.java.cloud.training.dto.product.UpdateProductDto;
import com.java.cloud.training.entity.Category;
import com.java.cloud.training.entity.Product;
import com.java.cloud.training.repository.ProductRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ProductControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Order(1)
    void shouldReturnProductByCode() throws Exception {
        Product product = generatedProducts.get(0);
        MvcResult response = mockMvc.perform(get("/products/{productCode}", product.getCode())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        ProductDto result = objectMapper.readValue(response.getResponse().getContentAsString(), ProductDto.class);

        assertThat(result.getCode()).isEqualTo(product.getCode());
        assertThat(result.getName()).isEqualTo(product.getName());
        assertThat(result.getCategories().size()).isEqualTo(product.getCategories().size());
    }

    @Test
    @Order(2)
    void shouldReturn404Status_whenCategoryByCodeNotFound() throws Exception {
        mockMvc.perform(get("/products/{productCode}", "emptyProductCode")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    void shouldReturnAllProductSortedByPriceAsc() throws Exception {
        MvcResult response = mockMvc.perform(get("/products")
                .param("sort", "price,asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ProductDto.class);
        List<ProductDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), type);

        assertThat(result.size()).isEqualTo(generatedProducts.size());
        checkThatProductOrderedByPriceAsc(result);
    }

    @Test
    @Order(4)
    void shouldFilterProductsByName() throws Exception {
        Product product = generatedProducts.get(0);

        MvcResult response = mockMvc.perform(get("/products")
                .param("name", product.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, ProductDto.class);
        List<ProductDto> result = objectMapper.readValue(response.getResponse().getContentAsString(), type);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategories().size()).isEqualTo(product.getCategories().size());
        assertThat(result.get(0).getName()).isEqualTo(product.getName());
        assertThat(result.get(0).getPrice()).isEqualTo(product.getPrice());
    }

    @Test
    @Order(5)
    void shouldUpdateProductByCode() throws Exception {
        Product product = generatedProducts.get(0);
        UpdateProductDto expectedDto = convertAndModifyDto(product);
        mockMvc.perform(put("/products/{productCode}", product.getCode())
                .content(objectMapper.writeValueAsString(expectedDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Product updatedProduct = productRepository
                .findByCode(product.getCode()).get();

        assertThat(updatedProduct.getName()).isEqualTo(expectedDto.getName());
        assertThat(updatedProduct.getPrice()).isEqualTo(expectedDto.getPrice());
    }

    @Test
    @Order(6)
    void shouldReturn404Status_whenTryToUpdateNotExistingProduct() throws Exception {
        Product product = generatedProducts.get(0);
        UpdateProductDto expectedDto = convertAndModifyDto(product);

        mockMvc.perform(put("/products/{productCode}", "emptyProductCode")
                .content(objectMapper.writeValueAsString(expectedDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    void shouldReturn404Status_whenTryToDeleteNotExistingProduct() throws Exception {
        mockMvc.perform(delete("/products/{productCode}", "emptyProductCode"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(8)
    void shouldDeleteProductByCode() throws Exception {
        String productCode = generatedProducts.get(0).getCode();
        mockMvc.perform(delete("/products/{productCode}", productCode))
                .andExpect(status().isOk());
        generatedProducts.remove(0);

        Optional<Product> product = productRepository.findByCode(productCode);

        assertThat(product.isPresent()).isFalse();
    }

    @Test
    @Order(9)
    void shouldCreateProduct() throws Exception {
        ProductDto dto = testDataGenerator.generateRandomData(ProductDto.class);
        dto.setCategories(generatedCategories.stream().map(Category::getCode).collect(Collectors.toList()));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        Optional<Product> createdProduct = productRepository.findByCode(dto.getCode());
        assertThat(createdProduct.isPresent()).isTrue();
    }

    @Test
    @Order(10)
    void shouldReturn409Status_whenTryToCreateProductWithExistingCode() throws Exception {
        ProductDto dto = testDataGenerator.generateRandomData(ProductDto.class);
        dto.setCategories(generatedCategories.stream().map(Category::getCode).collect(Collectors.toList()));
        dto.setCode(generatedProducts.get(0).getCode());

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    private UpdateProductDto convertAndModifyDto(Product product) {
        UpdateProductDto dto = new UpdateProductDto();
        dto.setName(product.getName() + randomInt(10));
        dto.setPrice(product.getPrice().add(BigDecimal.valueOf(randomInt(3))));
        return dto;
    }

    private void checkThatProductOrderedByPriceAsc(List<ProductDto> result) {
        if (result.size() <= 1) {
            return;
        }

        BigDecimal previousPrice;
        BigDecimal currentPrice;

        for (int index = 1; index < result.size(); index++) {
            previousPrice = result.get(index - 1).getPrice();
            currentPrice = result.get(index).getPrice();

            assertThat(previousPrice).isLessThanOrEqualTo(currentPrice);
        }
    }

}
