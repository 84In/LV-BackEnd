package com.luanvan.orderservice.repository;

import com.luanvan.orderservice.entity.Order;
import com.luanvan.orderservice.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Optional<Order> findByUserId(String userId);

    Page<Order> findAll(Specification<Order> specification, Pageable pageable);
}
