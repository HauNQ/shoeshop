package com.tech.shoeshop.entity.product;

import com.tech.shoeshop.entity.BaseEntity;
import com.tech.shoeshop.entity.order.OrderItem;
import com.tech.shoeshop.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "indx_category_id", columnList = "category_id"),
                @Index(name = "idx_product_category", columnList = "name, category_id"),
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 10, scale = 2)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "product",
            orphanRemoval = false)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void deductInventory(int stockQuantity){
        if(this.stockQuantity.equals(stockQuantity)){
            this.stockQuantity = 0;
            this.status = Status.SOLD_OUT;
        }

        if(this.stockQuantity > stockQuantity){
            this.stockQuantity -= stockQuantity;
        }
    }

    public void addInventory(int stockQuantity){

        if(this.stockQuantity.equals(0)){
            this.status = Status.ACTIVE;
        }

        this.stockQuantity += stockQuantity;
    }
}
