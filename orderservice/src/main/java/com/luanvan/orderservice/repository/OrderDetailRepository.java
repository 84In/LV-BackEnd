package com.luanvan.orderservice.repository;

import com.luanvan.orderservice.entity.Order;
import com.luanvan.orderservice.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
}
