package com.luanvan.productservice.repository;

import com.luanvan.productservice.entity.Category;
import com.luanvan.productservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
}
