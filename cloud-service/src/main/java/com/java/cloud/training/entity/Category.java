package com.java.cloud.training.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "categories")
public class Category implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(unique = true)
    @EqualsAndHashCode.Include
    private String code;
    @NotBlank
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name ="categories2products",
               joinColumns = {
                    @JoinColumn(name = "category_code", referencedColumnName = "code", nullable = false, updatable = false)
               },
               inverseJoinColumns = {
                    @JoinColumn(name = "product_code", referencedColumnName = "code", nullable = false, updatable = false)
               })
    private Set<Product> products = new HashSet();

    public void addProduct(Product product) {
        getProducts().add(product);
    }


    public void removeProduct(Product product) {
        getProducts().remove(product);
    }

}
