package com.tech.shoeshop.entity.order;

import com.tech.shoeshop.entity.BaseEntity;
import com.tech.shoeshop.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "order_items")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Override
    public boolean equals(Object o) {
        if(this == o) return  true;

        if(o == null || getClass() != o.getClass()) return false;

        OrderItem orderItem = (OrderItem) o;

        return id != null && id.equals(orderItem.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

    public void changeQuantity(int quantity){
        this.quantity = quantity;
        this.subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
