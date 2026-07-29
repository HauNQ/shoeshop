package com.tech.shoeshop.repository.product;

import com.tech.shoeshop.entity.product.Product;
import com.tech.shoeshop.enums.Status;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Override
    @EntityGraph(attributePaths = "category")
    Optional<Product> findById(Long id);

    // Derived Query Method
    List<Product> findByNameContainingAndStatus(String name, Status status);

    List<Product> findByPriceGreaterThanEqual(BigDecimal price);

    List<Product> findByPriceBetweenOrderByPriceAsc(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByCategory_NameIgnoreCase(String categoryName);

    boolean existsByCategoryId(Long categoryId);

    // JPQL
    @Query("""
             SELECT p 
             FROM Product p
             JOIN FETCH p.category
             WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    List<Product> findByNameContaining(@Param("name") String name);

    @Query("""
              SELECT p 
              FROM Product p    
              WHERE LOWER(p.category.name) = LOWER(:categoryName)
            """)
    List<Product> findByCategoryName(@Param("categoryName") String categoryName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p 
            FROM Product p
            WHERE p.id = :id
           """)
    Optional<Product> findByIdWithPessimisticLock(@Param("id") Long id);


    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
        UPDATE Product p
        SET p.stockQuantity = p.stockQuantity - :quantity
        WHERE p.id = :id AND p.stockQuantity >= :quantity
    """)
    int deductStock(@Param("id") Long productId, @Param("quantity")  Integer quantity);

    // Native SQL
    @Query(value = """
       SELECT *
       FROM products p
       WHERE p.price BETWEEN :minPrice AND :maxPrice
       """, nativeQuery = true)
    List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    @Query(value = """
           SELECT *
           FROM products p
           JOIN categories c
                ON p.categoryId = c.id
           WHERE c.name LIKE CONCAT('%', :categoryName ,'%')
           """, nativeQuery = true)
    List<Product> findByCategoryNameContain(@Param("categoryName") String categoryName);

    //Specification
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

}
