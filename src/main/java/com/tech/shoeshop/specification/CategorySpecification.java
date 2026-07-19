package com.tech.shoeshop.specification;

import com.tech.shoeshop.dto.request.category.CategoryFilterRequest;
import com.tech.shoeshop.entity.product.Category;
import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecification {

    private CategorySpecification() {
    }

    public static Specification<Category> filter(CategoryFilterRequest filterRequest){
        return Specification.where(hasName(filterRequest.getName()))
                .and(hasDescription(filterRequest.getDescription()));
    }

    public static Specification<Category> hasName(String name) {
        return ((root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%"
            );
        });

    }

    public static Specification<Category> hasDescription(String description){
        return ((root, query, criteriaBuilder) -> {
            if(description == null || description.isBlank()){
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")),
                    "%" + description.toLowerCase() + "%"
            );
        });
    }
}
