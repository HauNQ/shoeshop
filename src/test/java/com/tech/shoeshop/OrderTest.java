package com.tech.shoeshop;

import com.tech.shoeshop.service.order.OrderService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OrderTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    void testFindAll_NPlusOne() {

        Statistics statistics = entityManagerFactory
                .unwrap(SessionFactory.class)
                .getStatistics();

        statistics.clear();

        orderService.findAll();

        System.out.println("-----------------------------------");
        System.out.println("Queries executed: " + statistics.getPrepareStatementCount());
        System.out.println("-----------------------------------");
    }

    @Test
    void testFindAllWithDetails() {

        Statistics statistics = entityManagerFactory
                .unwrap(SessionFactory.class)
                .getStatistics();

        statistics.clear();

        orderService.findAllWithDetails();

        System.out.println("-----------------------------------");
        System.out.println("Queries executed: " + statistics.getPrepareStatementCount());
        System.out.println("-----------------------------------");
    }

    @Test
    void compareNPlusOneAndJoinFetch() {

        Statistics statistics = entityManagerFactory
                .unwrap(SessionFactory.class)
                .getStatistics();

        statistics.clear();

        orderService.findAll();

        long normalQueries = statistics.getPrepareStatementCount();

        statistics.clear();

        orderService.findAllWithDetails();

        long fetchQueries = statistics.getPrepareStatementCount();

        System.out.println("Normal query      : " + normalQueries);
        System.out.println("Join Fetch query  : " + fetchQueries);
    }
}
