package com.java.cloud.training.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.java.cloud.training.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor {

    Optional<Product> findByCode(String code);

    Set<Product> findByCodeIn(List<String> codes);
}
