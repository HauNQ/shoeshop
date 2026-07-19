package com.tech.shoeshop.dataSeeder;

import com.tech.shoeshop.entity.product.Category;
import com.tech.shoeshop.entity.product.Product;
import com.tech.shoeshop.enums.Status;
import com.tech.shoeshop.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class CategoryProductDataSeeder implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
//    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {

//        List<Category> categories = categoryRepository.findAll();
//        List<Product>  products   = productRepository.findAll();

        if (categoryRepository.count() > 0) {
            return;
        }

        seedCategoriesAndProducts();
    }

    private void seedCategoriesAndProducts() {

        Category sneakers = createSneakersCategory();
        Category boots = createBootsCategory();
        Category sandals = createSandalsCategory();

        categoryRepository.saveAll(List.of(
                sneakers,
                boots,
                sandals
        ));
    }

    private Category createSneakersCategory() {

        Category category = Category.builder()
                .name("Sneakers")
                .description("Casual and sports sneakers")
                .build();

        category.addProduct(createProduct(
                "Nike Air Force 1",
                "Classic white sneakers",
                "120.00",
                50
        ));

        category.addProduct(createProduct(
                "Adidas Ultraboost",
                "Running shoes with Boost cushioning",
                "180.00",
                30
        ));

        category.addProduct(createProduct(
                "Puma RS-X",
                "Retro style sneakers",
                "140.00",
                25
        ));

        return category;
    }

    private Category createBootsCategory() {

        Category category = Category.builder()
                .name("Boots")
                .description("Leather and outdoor boots")
                .build();

        category.addProduct(createProduct(
                "Timberland Premium Boot",
                "Waterproof leather boots",
                "220.00",
                20
        ));

        category.addProduct(createProduct(
                "Chelsea Boot",
                "Elegant ankle boots",
                "160.00",
                15
        ));

        return category;
    }

    private Category createSandalsCategory() {

        Category category = Category.builder()
                .name("Sandals")
                .description("Comfortable sandals for daily use")
                .build();

        category.addProduct(createProduct(
                "Crocs Classic",
                "Comfortable everyday sandals",
                "60.00",
                80
        ));

        category.addProduct(createProduct(
                "Birkenstock Arizona",
                "Premium leather sandals",
                "130.00",
                35
        ));

        return category;
    }

    private Product createProduct(
            String name,
            String description,
            String price,
            Integer stockQuantity
    ) {

        return Product.builder()
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .stockQuantity(stockQuantity)
                .status(Status.ACTIVE)
                .build();
    }
}
