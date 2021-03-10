package com.java.cloud.training.service;

import java.util.List;
import com.java.cloud.training.dto.product.ProductDto;
import com.java.cloud.training.service.data.ProductSearchData;
import org.springframework.transaction.annotation.Transactional;

public interface ProductService {

    List<ProductDto> getProducts(ProductSearchData searchData);

    ProductDto getProduct(String code);

    @Transactional
    void createProduct(ProductDto dto);

    @Transactional
    void deleteProduct(String code);

    @Transactional
    void updateProduct(ProductDto dto);

}
