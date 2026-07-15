package com.tech.shoeshop;

import com.tech.shoeshop.enums.Status;
import com.tech.shoeshop.repository.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class ProductTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testCallProductRepository(){
        var findById = productRepository.findById(6L);
        var findByNameContainingAndStatus = productRepository.findByNameContainingAndStatus("Air", Status.ACTIVE);
        var findByPriceGreaterThanEqual = productRepository.findByPriceGreaterThanEqual(BigDecimal.valueOf(180));
        var findByPriceBetweenOrderByPriceAsc = productRepository.findByPriceBetweenOrderByPriceAsc(BigDecimal.valueOf(60), BigDecimal.valueOf(120));
        var findByCategory_NameIgnoreCase = productRepository.findByCategory_NameIgnoreCase("boots");
        var findByNameContaining = productRepository.findByNameContaining("boo");
        var findByCategoryName = productRepository.findByCategoryName("Sandals");
        var findByPriceBetween = productRepository.findByPriceBetween(BigDecimal.valueOf(60), BigDecimal.valueOf(120));
        var findByCategoryNameContain = productRepository.findByCategoryNameContain("nda");
    }
}
