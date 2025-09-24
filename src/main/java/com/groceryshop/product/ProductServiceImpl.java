package com.groceryshop.product;

import com.groceryshop.shared.dto.request.CreateProductRequest;
import com.groceryshop.shared.dto.request.UpdateProductRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProductServiceImpl(ProductRepository productRepository, ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts(ProductSearchCriteria criteria) {
        Pageable pageable = PageRequest.of(
            criteria.page(),
            criteria.size(),
            Sort.by(Sort.Direction.fromString(criteria.sortDirection()), criteria.sortBy())
        );

        Specification<Product> spec = Specification.allOf();

        if (criteria.name() != null && !criteria.name().trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + criteria.name().toLowerCase() + "%"));
        }

        if (criteria.category() != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("category"), criteria.category()));
        }

        if (criteria.status() != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), criteria.status()));
        }

        if (criteria.minPrice() != null) {
            spec = spec.and((root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("price"), criteria.minPrice()));
        }

        if (criteria.maxPrice() != null) {
            spec = spec.and((root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), criteria.maxPrice()));
        }

        Page<Product> page = productRepository.findAll(spec, pageable);
        return page.getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    @Transactional
    public Product createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setCategory(request.category());
        product.setStockQuantity(request.stockQuantity());

        Product savedProduct = productRepository.save(product);

        // Publish product added event
        eventPublisher.publishEvent(new ProductAddedEvent(
            this,
            savedProduct.getId(),
            savedProduct.getName(),
            savedProduct.getStockQuantity()
        ));

        return savedProduct;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, UpdateProductRequest request) {
        Product product = getProductById(id);

        if (request.name() != null) product.setName(request.name());
        if (request.description() != null) product.setDescription(request.description());
        if (request.price() != null) product.setPrice(request.price());
        if (request.imageUrl() != null) product.setImageUrl(request.imageUrl());
        if (request.category() != null) product.setCategory(request.category());
        if (request.status() != null) product.setStatus(request.status());
        if (request.stockQuantity() != null) {
            int oldStock = product.getStockQuantity();
            product.setStockQuantity(request.stockQuantity());

            // Publish stock updated event if stock changed
            if (oldStock != request.stockQuantity()) {
                eventPublisher.publishEvent(new StockUpdatedEvent(
                    this,
                    product.getId(),
                    product.getName(),
                    oldStock,
                    request.stockQuantity()
                ));
            }
        }

        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        // Publish product updated event
        eventPublisher.publishEvent(new ProductUpdatedEvent(
            this,
            savedProduct.getId(),
            savedProduct.getName()
        ));

        return savedProduct;
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getRecommendations(Long userId) {
        // Simple recommendation logic - return products from different categories
        // In a real implementation, this would use user preferences, purchase history, etc.
        return productRepository.findTop10ByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategoryAndStatus(category, ProductStatus.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseAndStatus(query, ProductStatus.ACTIVE);
    }
}
