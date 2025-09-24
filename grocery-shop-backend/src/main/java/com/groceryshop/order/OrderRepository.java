package com.groceryshop.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStatusAndOrderDateBetween(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status IN :statuses ORDER BY o.orderDate DESC")
    List<Order> findByCustomerIdAndStatusIn(@Param("customerId") Long customerId, @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    java.math.BigDecimal sumTotalAmountByStatusAndDateRange(@Param("status") OrderStatus status,
                                                            @Param("startDate") LocalDateTime startDate,
                                                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status")
    java.util.Optional<java.math.BigDecimal> sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    long countByOrderDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.orderDate BETWEEN :startDate AND :endDate")
    long countByStatusAndOrderDateBetween(@Param("status") OrderStatus status,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT o.customer FROM Order o WHERE o.orderDate > :since")
    java.util.List<com.groceryshop.auth.User> findDistinctCustomersWithOrdersAfter(@Param("since") LocalDateTime since);

    @Query("SELECT o.customer, COUNT(o), SUM(o.totalAmount) FROM Order o GROUP BY o.customer ORDER BY SUM(o.totalAmount) DESC")
    java.util.List<java.lang.Object[]> findTopCustomersByOrderCount(@Param("limit") int limit);

    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status = :status")
    java.util.List<Order> findByOrderDateBetweenAndStatus(@Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate,
                                                          @Param("status") OrderStatus status);
}
