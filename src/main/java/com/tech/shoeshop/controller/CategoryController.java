package com.tech.shoeshop.controller;

import com.tech.shoeshop.common.response.ApiResponse;
import com.tech.shoeshop.common.response.PageResponse;
import com.tech.shoeshop.dto.request.category.CategoryFilterRequest;
import com.tech.shoeshop.dto.request.category.CategoryRequest;
import com.tech.shoeshop.dto.response.category.CategoryResponse;
import com.tech.shoeshop.service.category.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable("id")
            @Positive(message = "Category ID must be greater than 0")
            Long categoryId) {

        CategoryResponse response = categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK,
                "Category retrieved successfully",
                response
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getCategories(
            @ModelAttribute CategoryFilterRequest filterRequest,

            @PageableDefault(page = 0, size = 15)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "name", direction = Sort.Direction.ASC)
            })
            Pageable pageable)
    {

        PageResponse<CategoryResponse> response = categoryService.getCategories(filterRequest, pageable);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Categories retrieved successfully",
                        response
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest)
    {
        CategoryResponse response = categoryService.createCategory(categoryRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.success(
                        HttpStatus.CREATED,
                        "Created category successfully",
                        response
                ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable("id")
            @Positive(message = "Category ID must be greater than 0")
            Long id,
            @Valid
            @RequestBody
            CategoryRequest request)
    {
        CategoryResponse response = categoryService.updateCategory(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Updated category successfully",
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable("id")
            @Positive(message = "Category Id must be greater than 0")
            Long categoryId)
    {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        "Category deleted successfully"
                )
        );
    }


}

