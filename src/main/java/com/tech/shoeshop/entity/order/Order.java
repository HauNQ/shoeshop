package com.tech.shoeshop.entity.order;

import com.tech.shoeshop.entity.BaseEntity;
import com.tech.shoeshop.entity.auth.User;
import com.tech.shoeshop.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_user_id", columnList = "user_id"),
                @Index(name = "idx_orders_status", columnList = "status")
        }
)
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            mappedBy = "order",
            orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void addOrderItem(OrderItem orderItem) {
        Objects.requireNonNull(orderItem, "orderItem must not be null");

        this.orderItems.add(orderItem);
        orderItem.setOrder(this);

        totalAmount = totalAmount.add(orderItem.getSubTotal());
    }

    public void removeOrderItem(OrderItem orderItem) {
        Objects.requireNonNull(orderItem, "orderItem must not be null");

        if(this.orderItems.remove(orderItem)){
            orderItem.setOrder(null);
            totalAmount = totalAmount.subtract(orderItem.getSubTotal());
        }
    }
}
