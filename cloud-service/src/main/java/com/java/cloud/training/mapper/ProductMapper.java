package com.java.cloud.training.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.ConfigurableMapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.java.cloud.training.dto.product.ProductDto;
import com.java.cloud.training.entity.Category;
import com.java.cloud.training.entity.Product;
import com.java.cloud.training.repository.CategoryRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper extends ConfigurableMapper {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(Product.class, ProductDto.class)
                .exclude("categories")
                .exclude("id")
                .byDefault()
                .mapNulls(false)
                .customize(new ProductCustomMapper())
                .register();
    }


    private class ProductCustomMapper extends CustomMapper<Product, ProductDto> {

        @Override
        public void mapAtoB(Product product, ProductDto productDto, MappingContext context) {
            List<String> categoriesCodes = product.getCategories()
                    .stream()
                    .map(Category::getCode)
                    .collect(Collectors.toList());
            productDto.setCategories(categoriesCodes);
        }

        @Override
        public void mapBtoA(ProductDto productDto, Product product, MappingContext context) {
            if (CollectionUtils.isEmpty(productDto.getCategories())) {
                return;
            }
            Set<Category> oldCategories = product.getCategories();
            Set<Category> newCategories = categoryRepository.findByCodeIn(productDto.getCategories());
            product.addCategories(newCategories);

            Collection<Category> substract = CollectionUtils.subtract(oldCategories, newCategories);
            substract.forEach(category -> category.removeProduct(product));
            categoryRepository.saveAll(substract);
        }
    }
}
