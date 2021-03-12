package com.java.cloud.training;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.java.cloud.training.entity.Category;
import com.java.cloud.training.entity.Product;
import com.java.cloud.training.repository.CategoryRepository;
import com.java.cloud.training.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.github.benas.randombeans.EnhancedRandomBuilder.aNewEnhancedRandom;

@Component
public class TestDataGenerator {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    private EnhancedRandom enhancedRandom = EnhancedRandomBuilder.aNewEnhancedRandom();

    public List<Category> generateCategories(int count) {
        List<Category> categories = enhancedRandom.objects(Category.class, count, "id")
                .collect(Collectors.toList());
        categoryRepository.saveAll(categories);
        return categories;
    }

    public List<Product> generateProductsForCategories(Map<Set<Category>, Integer> categoriesAndProductsCount) {
        List<Product> generatedProducts = new ArrayList<>();
        Set<Product> products;
        for (Map.Entry<Set<Category>, Integer> entry: categoriesAndProductsCount.entrySet()){
            products = enhancedRandom.objects(Product.class, entry.getValue(), "id")
                    .collect(Collectors.toSet());
            productRepository.saveAll(products);
            assignProductsToCategories(entry.getKey(), products);
            generatedProducts.addAll(products);
        }
        return generatedProducts;
     }

     public <T> T generateRandomData(Class<T> clazz, String... excludedAttrubutes) {
        return enhancedRandom.nextObject(clazz, excludedAttrubutes);
     }

     private void assignProductsToCategories(Set<Category> categories, Set<Product> products) {
        categories.forEach(category -> category.setProducts(products));
        categoryRepository.saveAll(categories);
     }
}
