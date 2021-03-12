package com.java.cloud.training.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.cloud.training.TestDataGenerator;
import com.java.cloud.training.entity.Category;
import com.java.cloud.training.entity.Product;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AbstractControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected TestDataGenerator testDataGenerator;

    protected List<Category> generatedCategories;
    protected List<Product> generatedProducts;


    private boolean generated;

    @BeforeEach
    public void generate() {
        if (generated) {
            return;
        }
        generatedCategories = testDataGenerator.generateCategories(randomInt(5));
        generatedProducts = testDataGenerator.generateProductsForCategories(prepareForProductsGeneration(generatedCategories));
    }

    protected int randomInt(int maxNumber) {
        return ThreadLocalRandom.current().nextInt(2, maxNumber + 1);
    }

    protected Map<Set<Category>, Integer> prepareForProductsGeneration(List<Category> categories) {
        return categories.stream()
                .collect(Collectors.toMap(category -> Sets.newHashSet(asList(category)), category -> randomInt(3)));
    }
}
