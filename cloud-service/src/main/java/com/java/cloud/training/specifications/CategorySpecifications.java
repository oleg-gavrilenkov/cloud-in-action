package com.java.cloud.training.specifications;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.SetJoin;
import com.java.cloud.training.entity.Category;
import com.java.cloud.training.entity.Category_;
import com.java.cloud.training.entity.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecifications {

    private CategorySpecifications() {
    }

    public static Specification<Category> nameContain(String name) {
        return (root, criteriaQuery, criteriaBuilder) ->  {
            if (StringUtils.isNoneEmpty(name)) {
                return criteriaBuilder.like(root.get(Category_.NAME), "%" + name + "%");
            }
            return null;
        };
    }

    public static Specification<Category> orderByProductsCount(Sort.Direction direction) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            SetJoin<Category, Product> products = root.join(Category_.products, JoinType.LEFT);
            criteriaQuery.groupBy(root.get(Category_.id));
            if (direction == Sort.Direction.ASC) {
                criteriaQuery.orderBy(criteriaBuilder.asc(criteriaBuilder.count(products)));
            } else {
                criteriaQuery.orderBy(criteriaBuilder.desc(criteriaBuilder.count(products)));
            }
            criteriaQuery.distinct(true);
            return null;
        };
    }
}
