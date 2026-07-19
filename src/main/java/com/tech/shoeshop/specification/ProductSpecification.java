package com.tech.shoeshop.specification;

import com.tech.shoeshop.dto.request.product.ProductFilterRequest;
import com.tech.shoeshop.entity.product.Product;
import com.tech.shoeshop.enums.Status;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> filter(ProductFilterRequest request){
        return Specification.where(hasName(request.getName()))
                .and(hasCategoryName(request.getCategoryName()))
                .and(priceBetween(request.getMinPrice(), request.getMaxPrice()))
                .and(hasStatus(request.getStatus()));
    }

    public static Specification<Product> hasName(String name) {

        return ((root, query, criteriaBuilder) ->
        {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        }
        );
    }

    public static Specification<Product> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) -> {

            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }

            if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> hasCategoryName(String categoryName) {

        return ((root, query, criteriaBuilder) ->
            {
                if (categoryName == null || categoryName.isBlank()) {
                    return criteriaBuilder.conjunction();
                }

                return criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("category").get("name")),
                        "%" + categoryName.toLowerCase() + "%"
                );
            }
        );
    }

    public static Specification<Product> hasStatus(Status status) {

        return ((root, query, criteriaBuilder) ->
            {
                if (status == null || status.name().isBlank()) {
                    return criteriaBuilder.conjunction();
                }

                return criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
            }
        );
    }
}
