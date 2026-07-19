package com.tech.shoeshop.service.impl.category;

import com.tech.shoeshop.common.response.PageResponse;
import com.tech.shoeshop.dto.request.category.CategoryFilterRequest;
import com.tech.shoeshop.dto.request.category.CategoryRequest;
import com.tech.shoeshop.dto.response.category.CategoryResponse;
import com.tech.shoeshop.entity.product.Category;
import com.tech.shoeshop.exception.BadRequestException;
import com.tech.shoeshop.exception.CategoryInUseException;
import com.tech.shoeshop.exception.DuplicateCategoryNameException;
import com.tech.shoeshop.exception.ResourceNotFoundException;
import com.tech.shoeshop.mapper.CategoryMapper;
import com.tech.shoeshop.repository.category.CategoryRepository;
import com.tech.shoeshop.repository.product.ProductRepository;
import com.tech.shoeshop.service.category.CategoryService;
import com.tech.shoeshop.specification.CategorySpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "createdAt");

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.debug("Creating category '{}'", request.getName());

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            log.warn("Category '{}' already exists.", request.getName());
            throw new DuplicateCategoryNameException(request.getName());
        }

        Category category = categoryRepository.save(categoryMapper.toEntity(request));

        log.debug("Created category '{}' successfully", category.getName());

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        log.debug("Updating category with ID {}", categoryId);

        Category category = getCategoryByIdOrThrow(categoryId);

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), categoryId)) {
            log.warn("Category '{}' already exists.", request.getName());
            throw new DuplicateCategoryNameException(request.getName());
        }

        categoryMapper.updateEntity(request, category);

        category = categoryRepository.save(category);

        log.debug("Updated category with ID {} successfully", category.getId());

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        log.debug("Deleting category with ID {}", categoryId);

        Category category = getCategoryByIdOrThrow(categoryId);

        if(productRepository.existsByCategoryId(categoryId)){
            log.warn("Cannot delete category with ID {} because it is assigned to one or more products", categoryId);
            throw new CategoryInUseException(categoryId);
        }

        categoryRepository.delete(category);
        log.debug("Deleted category with ID {} successfully", categoryId);
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {
        log.debug("Retrieving category with ID {}", categoryId);

        return categoryMapper.toResponse(getCategoryByIdOrThrow(categoryId));
    }

    @Override
    public PageResponse<CategoryResponse> getCategories(CategoryFilterRequest filterRequest, Pageable pageable) {
        validateSort(pageable);

        log.debug(
                "Filtering categories: name={}, description={}",
                filterRequest.getName(),
                filterRequest.getDescription()
        );

        Specification<Category> spec = CategorySpecification.filter(filterRequest);

        Page<CategoryResponse> page = categoryRepository
                .findAll(spec, pageable)
                .map(categoryMapper::toResponse);


        return PageResponse.<CategoryResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElement(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    private Category getCategoryByIdOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.warn("Category with ID {} not found", categoryId);
                    return new ResourceNotFoundException(
                            "Category not found with ID " + categoryId);
                });
    }

    private void validateSort(Pageable pageable){
        for(Sort.Order order : pageable.getSort()){
            if(!ALLOWED_SORT_FIELDS.contains(order.getProperty())){
                throw new BadRequestException("Sorting by '%s' is not supported".formatted(order.getProperty()));
            }
        }
    }
}
