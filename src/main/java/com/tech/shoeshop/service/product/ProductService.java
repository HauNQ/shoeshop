package com.tech.shoeshop.service.product;

import com.tech.shoeshop.common.response.PageResponse;
import com.tech.shoeshop.dto.request.product.ProductFilterRequest;
import com.tech.shoeshop.dto.request.product.ProductRequest;
import com.tech.shoeshop.dto.response.product.ProductResponse;
import com.tech.shoeshop.entity.product.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    /**
     * Create a new product.
     *
     * @param request product information to create
     * @return the created product
     * */
    ProductResponse createProduct(ProductRequest request);

    /**
     * Update an existing product.
     *
     * @param productId the ID of the product to update
     * @param request the updated product information
     * @return the updated product
     * */
    ProductResponse updateProduct(Long productId,ProductRequest request);

    /**
     * Delete a product by its ID.
     *
     * @param productId the ID of the product to delete
     * */
    void deleteProduct(Long productId);

    /**
     * Get product by its ID.
     *
     * @param productId the ID of the product
     * @return the product information
     * */
    ProductResponse getProductById(Long productId);

    /**
     * Get products.
     *
     * @param product information
     * @param pageable
     * @return the product information
     * */
    PageResponse<ProductResponse> getProducts(ProductFilterRequest request, Pageable pageable);
}
