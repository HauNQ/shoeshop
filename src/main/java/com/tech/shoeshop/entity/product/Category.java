package com.tech.shoeshop.entity.product;

import com.tech.shoeshop.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "category", orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product){

        if (product == null) {
            return;
        }
        products.add(product);
//        if (!products.contains(product)) {
//            products.add(product);
//        }

        product.setCategory(this);
    }

    public void removeProduct(Product product){

        if (product == null) {
            return;
        }

        products.remove(product);
        product.setCategory(null);
    }
}
