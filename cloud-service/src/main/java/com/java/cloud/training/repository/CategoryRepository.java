package com.java.cloud.training.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.java.cloud.training.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor {

    Optional<Category> findByCode(String code);

    Set<Category> findByCodeIn(List<String> codes);

}
