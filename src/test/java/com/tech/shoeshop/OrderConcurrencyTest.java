package com.tech.shoeshop;

import com.tech.shoeshop.dto.request.order.OrderItemRequest;
import com.tech.shoeshop.dto.request.order.OrderRequest;
import com.tech.shoeshop.entity.product.Category;
import com.tech.shoeshop.entity.product.Product;
import com.tech.shoeshop.enums.Status;
import com.tech.shoeshop.repository.auth.UserRepository;
import com.tech.shoeshop.repository.category.CategoryRepository;
import com.tech.shoeshop.repository.product.ProductRepository;
import com.tech.shoeshop.service.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class OrderConcurrencyTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private Long productId;
    private Long userId;

    @BeforeEach
    void setUp() {

        Product product = Product.builder()
                .name("Race Condition Test Product 3")
                .description("aaaa")
                .status(Status.ACTIVE)
                .category(categoryRepository.findById(6L).get())
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .build();

        product = productRepository.saveAndFlush(product);

        productId = product.getId();
    }

    @Test
    void should_test_race_condition() throws Exception {

        int concurrentRequests = 10;
        int quantityPerRequest = 2;

        ExecutorService executor =
                Executors.newFixedThreadPool(concurrentRequests);

        CountDownLatch ready =
                new CountDownLatch(concurrentRequests);

        CountDownLatch start =
                new CountDownLatch(1);

        CountDownLatch done =
                new CountDownLatch(concurrentRequests);

        AtomicInteger successCount =
                new AtomicInteger();

        AtomicInteger failureCount =
                new AtomicInteger();

        for (int i = 0; i < concurrentRequests; i++) {

            executor.submit(() -> {

                try {

                    // Mỗi thread phải có SecurityContext riêng
                    SecurityContext context =
                            SecurityContextHolder.createEmptyContext();

                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(
                                    "hau1",
                                    "hau123123"
                            );

                    context.setAuthentication(authentication);

                    SecurityContextHolder.setContext(context);

                    ready.countDown();

                    // Chờ tất cả threads sẵn sàng
                    start.await();

                    orderService.createOrder(
                            createOrderRequest()
                    );

                    successCount.incrementAndGet();

                } catch (Exception e) {

                    failureCount.incrementAndGet();

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " FAILED: "
                                    + e.getClass().getName()
                                    + " - "
                                    + e.getMessage()
                    );

                } finally {

                    SecurityContextHolder.clearContext();

                    done.countDown();
                }
            });
        }

        // Đảm bảo tất cả thread đã ready
        ready.await();

        // Bắt đầu race
        start.countDown();

        // Chờ tất cả request hoàn thành
        done.await();

        executor.shutdown();

        Product product =
                productRepository.findById(productId)
                        .orElseThrow();

        int finalStock =
                product.getStockQuantity();

        int totalSold =
                successCount.get() * quantityPerRequest;

        System.out.println("================================");
        System.out.println("Success     = " + successCount.get());
        System.out.println("Failure     = " + failureCount.get());
        System.out.println("Total sold  = " + totalSold);
        System.out.println("Final stock = " + finalStock);
        System.out.println("================================");
    }

    private OrderRequest createOrderRequest() {

        OrderItemRequest item = new OrderItemRequest();

        item.setProductId(productId);
        item.setQuantity(2);

        OrderRequest request = new OrderRequest();

        request.setItems(List.of(item));

        return request;
    }

}
