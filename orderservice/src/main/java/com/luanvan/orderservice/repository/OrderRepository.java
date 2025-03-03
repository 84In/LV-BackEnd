package com.luanvan.orderservice.repository;

import com.luanvan.orderservice.entity.Order;
import com.luanvan.orderservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
}
