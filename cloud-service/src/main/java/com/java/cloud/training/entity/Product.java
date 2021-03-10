package com.java.cloud.training.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(unique = true)
    private String code;
    @NotBlank
    private String name;
    @NotNull
    private BigDecimal price;
    @ManyToMany(mappedBy = "products", fetch = FetchType.LAZY)
    private Set<Category> categories = new HashSet();

    public void addCategories(Set<Category> categories) {
        getCategories().addAll(categories);
        categories.forEach(category -> category.addProduct(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (getId() == null) {
            return false;
        }
        Product product = (Product) o;
        return getCode().equals(product.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode());
    }
}
