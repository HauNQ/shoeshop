package com.tech.shoeshop.controller;

import com.tech.shoeshop.common.response.ApiResponse;
import com.tech.shoeshop.dto.request.product.ProductFilterRequest;
import com.tech.shoeshop.dto.request.product.ProductRequest;
import com.tech.shoeshop.dto.response.product.ProductResponse;
import com.tech.shoeshop.service.product.ProductService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable("id")
            @Positive(message = "Product ID must be greater than 0")
            Long productId)
    {
        ProductResponse response = productService.getProductById(productId);

        return ResponseEntity.ok(
          ApiResponse.success(
                  HttpStatus.OK,
                  "Product retrieved successfully",
                  response
          )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(@Valid @ModelAttribute ProductFilterRequest request)
    {
        List<ProductResponse> response = productService.getProducts(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Products retrieved successfully",
                        response
                )
        );
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request){

        ProductResponse response = productService.createProduct(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(
                        ApiResponse.success(
                            HttpStatus.CREATED,
                            "Product created successfully",
                            response
                        )
                );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable("id")
            @Positive(message = "Product ID must be greater than 0")
            Long productId,
            @Valid
            @RequestBody
            ProductRequest request)
    {
        ProductResponse productResponse = productService.updateProduct(productId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Product updated successfully",
                        productResponse
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable("id")
            @Positive(message = "Product ID must be greater than 0")
            Long productId)
    {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(
                  ApiResponse.success(
                          HttpStatus.OK,
                          "Product deleted successfully")
                );
    }

}
