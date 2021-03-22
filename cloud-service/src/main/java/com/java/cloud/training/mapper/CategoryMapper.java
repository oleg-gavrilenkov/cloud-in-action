package com.java.cloud.training.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.java.cloud.training.dto.category.CategoryDto;
import com.java.cloud.training.dto.product.ProductDto;
import com.java.cloud.training.entity.Category;
import com.java.cloud.training.entity.Product;
import com.java.cloud.training.repository.ProductRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Component
public class CategoryMapper extends ConfigurableMapper {

    @Autowired
    private ProductRepository productRepository;

    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(Category.class, CategoryDto.class)
                .exclude("products")
                .exclude("id")
                .byDefault()
                .mapNulls(false)
                .customize(new CategoryCustomerMapper())
                .register();
    }

    private class CategoryCustomerMapper extends CustomMapper<Category, CategoryDto> {

        @Override
        public void mapAtoB(Category category, CategoryDto categoryDto, MappingContext context) {
            List<String> productsCodes = category.getProducts()
                    .stream()
                    .map(Product::getCode)
                    .collect(Collectors.toList());
            categoryDto.setProducts(productsCodes);
        }

        @Override
        public void mapBtoA(CategoryDto categoryDto, Category category, MappingContext context) {
            category.setProducts(getNewProducts(categoryDto));
        }
        
        private Set<Product> getNewProducts(CategoryDto categoryDto) {
        	List<String> productsCodes = categoryDto.getProducts();
        	return isEmpty(productsCodes) ? new HashSet<>() : productRepository.findByCodeIn(productsCodes);
        }

    }
}
