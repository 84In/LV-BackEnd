package com.luanvan.userservice.repository;

import com.luanvan.userservice.entity.CartDetail;
import com.luanvan.userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, String> {
    boolean existsByProductIdAndColorIdAndSizeId(String productId, String colorId, String sizeId);
}
