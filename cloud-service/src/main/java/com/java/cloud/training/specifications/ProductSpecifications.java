package com.java.cloud.training.specifications;

import java.math.BigDecimal;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.SetJoin;
import com.java.cloud.training.entity.Category;
import com.java.cloud.training.entity.Category_;
import com.java.cloud.training.entity.Product;
import com.java.cloud.training.entity.Product_;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> relateToCategory(String categoryCode) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (isNotEmpty(categoryCode)) {
                SetJoin<Product, Category> categoryJoin = root.join(Product_.categories);
                return criteriaBuilder.equal(categoryJoin.get(Category_.CODE), categoryCode);
            }
            return null;
        };
    }

    public static Specification<Product> priceRange(BigDecimal low, BigDecimal high) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (nonNull(low) && nonNull(high)) {
                return criteriaBuilder.between(root.get(Product_.PRICE), low, high);
            } else if (nonNull(low)) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(Product_.PRICE), low);
            } else if (nonNull(high)) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(Product_.PRICE), high);
            }
            return null;
        };
    }

    public static Specification<Product> nameContain(String searchValue) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (isNotEmpty(searchValue)) {
                return criteriaBuilder.like(root.get(Product_.NAME), "%" + searchValue + "%");
            }
            return null;
        };
    }

}
