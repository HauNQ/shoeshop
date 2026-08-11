package com.tech.shoeshop.service.impl.product;

import com.tech.shoeshop.common.response.PageResponse;
import com.tech.shoeshop.dto.request.product.ProductFilterRequest;
import com.tech.shoeshop.dto.request.product.ProductRequest;
import com.tech.shoeshop.dto.response.product.ProductResponse;
import com.tech.shoeshop.entity.product.Category;
import com.tech.shoeshop.entity.product.Product;
import com.tech.shoeshop.exception.BadRequestException;
import com.tech.shoeshop.exception.ResourceNotFoundException;
import com.tech.shoeshop.mapper.ProductMapper;
import com.tech.shoeshop.repository.category.CategoryRepository;
import com.tech.shoeshop.repository.product.ProductRepository;
import com.tech.shoeshop.service.product.ProductService;
import com.tech.shoeshop.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "price", "createdAt");

    @Override
    @CacheEvict(value = "productList", allEntries = true)
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating a new product with name '{}'", request.getName());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                {
                    log.warn("Category with id '{}' not found", request.getCategoryId());
                    return new ResourceNotFoundException("Category not found with id " + request.getCategoryId());
                });

        Product newProduct = productMapper.toEntity(request);
        newProduct.setCategory(category);

        newProduct = productRepository.save(newProduct);

        log.info("Product '{}' is created successfully with id '{}'",
                newProduct.getName(),
                newProduct.getId());

        return productMapper.toResponse(newProduct);
    }

    @Override
    @Caching( evict=  {
        @CacheEvict(value = "products", key = "#productId"),
        @CacheEvict(value = "productList", allEntries = true)
    })
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        log.info("Updating product with id {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                {
                    log.warn("Product with id {} not found", productId);
                    return new ResourceNotFoundException("Product not found with id " + productId);
                });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                {
                    log.warn("Category with id '{}' not found", request.getCategoryId());
                    return new ResourceNotFoundException("Category not found with id " + request.getCategoryId());
                });

        productMapper.updateEntity(request, product);
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        log.info("Product '{}' with id {} updated successfully", updatedProduct.getName(), updatedProduct.getId());

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Caching( evict=  {
            @CacheEvict(value = "products", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    @Transactional
    public void deleteProduct(Long productId) {
        log.info("Delete product with id {}", productId);

        if (!productRepository.existsById(productId)) {
            log.warn("Product with id {} not found", productId);
            throw new ResourceNotFoundException(
                    "Product not found with id " + productId);
        }

        productRepository.deleteById(productId);

        log.info("Product with id {} deleted successfully", productId);
    }

    @Cacheable(value = "products", key = "#productId")
    @Override
    public ProductResponse getProductById(Long productId) {
        log.info("Retrieving product with id {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                {
                    log.warn("Product with id {} not found", productId);
                    return new ResourceNotFoundException("Product not found with id " + productId);
                });

        log.info("Product with id {} retrieved successfully", productId);

        return productMapper.toResponse(product);
    }

    @Cacheable(
            value = "productList",
            key = "#request.name + '-' + " +
                    "#request.categoryName + '-' + " +
                    "#request.minPrice + '-' + " +
                    "#request.maxPrice + '-' + " +
                    "#request.status + '-' + " +
                    "#pageable.pageNumber + '-' + " +
                    "#pageable.pageSize + '-' + " +
                    "#pageable.sort"
    )
    public PageResponse<ProductResponse> getProducts(ProductFilterRequest request, Pageable pageable) {

        validateSort(pageable);

        log.info(
                "Filtering products: name={}, category={}, minPrice={}, maxPrice={}, status={}",
                request.getName(),
                request.getCategoryName(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getStatus()
        );

        Specification<Product> spec = ProductSpecification.filter(request);

        Page<Product> productPage = productRepository.findAll(spec, pageable);

        List<ProductResponse> products = productPage
                .get()
                .map(productMapper::toResponse)
                .toList();

        return PageResponse.<ProductResponse>builder()
                .content(products)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElement(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }

    private void validateSort(Pageable pageable){
        for(Sort.Order order : pageable.getSort()){
            if(!ALLOWED_SORT_FIELDS.contains(order.getProperty())){
                throw new BadRequestException("Sorting by '%s' is not supported".formatted(order.getProperty()));
            }
        }
    }
}
