package com.tech.shoeshop.repository.order;

import com.tech.shoeshop.entity.order.Order;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @EntityGraph( attributePaths = {
            "user",
            "orderItems",
            "orderItems.product",
            "orderItems.product.category",
        }
    )
    Optional<Order> findOrderById(Long id);


    @EntityGraph( attributePaths = {
            "user",
            "orderItems",
            "orderItems.product",
            "orderItems.product.category",
        }
    )
    List<Order> findAll();

    @Query("""
        SELECT o
        FROM Order o
        JOIN FETCH o.user u
        JOIN FETCH o.orderItems oi
        JOIN FETCH oi.product p
        JOIN FETCH p.category c
        WHERE o.id = :id
    """)
    Optional<Order> findOrderDetailById(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT o
        FROM Order o
        JOIN FETCH o.user u
        JOIN FETCH o.orderItems oi
        JOIN FETCH oi.product p
        JOIN FETCH p.category c
    """)
    List<Order> findAllWithDetails();
}
