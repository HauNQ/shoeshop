package com.tech.shoeshop.mapper;

import com.tech.shoeshop.dto.request.category.CategoryRequest;
import com.tech.shoeshop.dto.response.category.CategoryResponse;
import com.tech.shoeshop.entity.product.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntity(CategoryRequest request, @MappingTarget Category category);
}
