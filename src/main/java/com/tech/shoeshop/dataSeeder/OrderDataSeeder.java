package com.tech.shoeshop.dataSeeder;

import com.tech.shoeshop.entity.auth.User;
import com.tech.shoeshop.entity.order.Order;
import com.tech.shoeshop.entity.order.OrderItem;
import com.tech.shoeshop.entity.product.Product;
import com.tech.shoeshop.enums.OrderStatus;
import com.tech.shoeshop.repository.auth.UserRepository;
import com.tech.shoeshop.repository.order.OrderRepository;
import com.tech.shoeshop.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Profile("dev")
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDataSeeder implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        if(orderRepository.count() > 0) {
            return;
        }

        User user = userRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->  new IllegalStateException("No user found"));

        List<Product> products = productRepository.findAll();

        if(products.size() < 2){
            throw new IllegalStateException("At least 2 products are required");
        }

        Product product1 = products.get(0);
        Product product2 = products.get(1);

        OrderItem orderItem1 = OrderItem.builder()
                .product(product1)
                .quantity(2)
                .unitPrice(product1.getPrice())
                .subTotal(product1.getPrice().multiply(BigDecimal.valueOf(2)))
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .product(product2)
                .quantity(1)
                .unitPrice(product2.getPrice())
                .subTotal(product2.getPrice())
                .build();

        BigDecimal totalAmount = orderItem1.getSubTotal().add(orderItem2.getSubTotal());

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.DELIVERED)
                .build();

        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        orderRepository.save(order);

        log.info("Order seed completed.");
    }
}
