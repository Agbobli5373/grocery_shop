package com.groceryshop.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByStatus(ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.stockQuantity <= :threshold")
    List<Product> findLowStockProducts(@Param("status") ProductStatus status, @Param("threshold") Integer threshold);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.status = :status")
    List<Product> findByNameContainingIgnoreCaseAndStatus(@Param("name") String name, @Param("status") ProductStatus status);

    List<Product> findByStatusOrderByCreatedAtDesc(ProductStatus status);

    List<Product> findByCategoryAndStatus(ProductCategory category, ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Product> findTop10ByStatusOrderByCreatedAtDesc(@Param("status") ProductStatus status);

    boolean existsByName(String name);

    long countByStockQuantityLessThan(int stockQuantity);

    long countByStockQuantity(int stockQuantity);

    List<Product> findByStockQuantityLessThan(int stockQuantity);

    long countByStatusAndStockQuantityGreaterThan(ProductStatus status, int stockQuantity);
}
