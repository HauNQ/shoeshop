package com.tech.shoeshop.mapper;

import com.tech.shoeshop.dto.request.product.ProductRequest;
import com.tech.shoeshop.dto.response.product.ProductResponse;
import com.tech.shoeshop.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductRequest request);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target =  "category", ignore = true)
    void updateEntity(ProductRequest request, @MappingTarget Product product);
}
