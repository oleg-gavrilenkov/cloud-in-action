package com.java.cloud.training.service.impl;

import java.util.List;
import java.util.Optional;
import com.java.cloud.training.dto.product.ProductDto;
import com.java.cloud.training.entity.Product;
import com.java.cloud.training.mapper.ProductMapper;
import com.java.cloud.training.repository.ProductRepository;
import com.java.cloud.training.service.ProductService;
import com.java.cloud.training.service.data.ProductSearchData;
import com.java.cloud.training.service.exception.EntityAlreadyExistsException;
import com.java.cloud.training.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import static com.java.cloud.training.specifications.ProductSpecifications.nameContain;
import static com.java.cloud.training.specifications.ProductSpecifications.priceRange;
import static com.java.cloud.training.specifications.ProductSpecifications.relateToCategory;

import static com.java.cloud.training.service.utils.SortUtils.parseSort;

@Component
public class ProductServiceImpl implements ProductService {

    private static final String DEFAULT_SORT = "name,asc";

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductDto> getProducts(ProductSearchData searchData) {
        Specification<Product> searchSpecification = buildSpecification(searchData);
        Sort sort = buildSort(searchData);
        List<Product> products = productRepository.findAll(searchSpecification, sort);
        return productMapper.mapAsList(products, ProductDto.class);
    }

    @Override
    public ProductDto getProduct(String code) {
       return productRepository.findByCode(code)
                .map(product -> productMapper.map(product, ProductDto.class))
                .orElseThrow(() -> new EntityNotFoundException("Product with code " + code + " not found"));
    }

    @Override
    public ProductDto createProduct(ProductDto dto) {
        if (productRepository.findByCode(dto.getCode()).isPresent()) {
            throw new EntityAlreadyExistsException("Product with code " + dto.getCode() + " already exists");
        } else {
            Product product = productMapper.map(dto, Product.class);
            productRepository.save(product);
            return productMapper.map(product, ProductDto.class);
        }
    }

    @Override
    public void deleteProduct(String code) {
        productRepository.findByCode(code)
                .ifPresentOrElse(
                        productRepository::delete,
                        () -> {
                            throw new EntityNotFoundException("Product with code " + code + " not found");
                        }
                );
    }

    @Override
    public void updateProduct(ProductDto dto) {
        productRepository.findByCode(dto.getCode())
                .ifPresentOrElse(
                        product -> {
                            productMapper.map(dto, product);
                            productRepository.save(product);
                        },
                        () -> {throw new EntityNotFoundException("Product with code " + dto.getCode() + " not found");}
                );
    }

    private Specification<Product> buildSpecification(ProductSearchData searchData) {
        return nameContain(searchData.getName())
                .and(priceRange(searchData.getPriceLow(), searchData.getPriceHigh()))
                .and(relateToCategory(searchData.getCategoryCode()));
    }

    private Sort buildSort(ProductSearchData searchData) {
        String sortStr = searchData.getSort();
        return parseSort(sortStr, DEFAULT_SORT);
    }
}
