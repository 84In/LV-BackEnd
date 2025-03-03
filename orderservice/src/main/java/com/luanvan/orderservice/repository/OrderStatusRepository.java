package com.luanvan.orderservice.repository;

import com.luanvan.orderservice.entity.OrderStatus;
import com.luanvan.orderservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, String> {
    OrderStatus findByCodeName(String pendingStatus);
}
