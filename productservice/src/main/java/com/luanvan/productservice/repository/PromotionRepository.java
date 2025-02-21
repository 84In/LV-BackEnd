package com.luanvan.productservice.repository;

import com.luanvan.productservice.entity.ProductColor;
import com.luanvan.productservice.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, String> {
    boolean existsByName(String name);
}
