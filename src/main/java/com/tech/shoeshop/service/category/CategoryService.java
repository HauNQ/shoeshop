package com.tech.shoeshop.service.category;

import com.tech.shoeshop.common.response.PageResponse;
import com.tech.shoeshop.dto.request.category.CategoryFilterRequest;
import com.tech.shoeshop.dto.request.category.CategoryRequest;
import com.tech.shoeshop.dto.response.category.CategoryResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    /**
     * Create a new category
     *
     * @param request category information to create
     * @return the created category
     * */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * Update an existed category
     *
     * @param categoryId the ID of the category to update
     * @param request category information to update
     * @return updated category
     * */
    CategoryResponse updateCategory(Long categoryId,CategoryRequest request);

    /**
     * Delete an existed category
     *
     * @param categoryId the ID of the category to delete
     * */
    void deleteCategory(Long categoryId);

    /**
     * Get category by its ID
     *
     * @param categoryId the ID of the category
     * @return category information
     * */
    CategoryResponse getCategoryById(Long categoryId);

    /**
     * Get category
     * @return list of category information
     * */
    PageResponse<CategoryResponse> getCategories(CategoryFilterRequest filterRequest, Pageable pageable);
}
