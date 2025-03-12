package com.luanvan.orderservice.repository;

import com.luanvan.commonservice.entity.PaymentStatus;
import com.luanvan.orderservice.entity.Order;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByUserId(String userId);

    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN o.payment p " +
            "WHERE o.orderStatus.codeName = 'confirmed' " +
            "AND p IS NOT NULL " +          // Chỉ lấy đơn có Payment
            "AND p.status = :status " +     // Chỉ lấy đơn hàng trạng thái PENDING
            "AND p.createdAt <= :expiredThreshold") // Payment quá 24h
    List<Order> findExpiredOrders(@Param("status") PaymentStatus status,
                                  @Param("expiredThreshold") LocalDateTime expiredThreshold);
}
