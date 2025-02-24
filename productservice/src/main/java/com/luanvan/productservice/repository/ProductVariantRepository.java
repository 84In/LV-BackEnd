package com.luanvan.productservice.repository;

import com.luanvan.productservice.entity.Category;
import com.luanvan.productservice.entity.ProductColor;
import com.luanvan.productservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    List<ProductVariant> findAllByProductColor(ProductColor productColor);
    Optional<ProductVariant> findByProductColorIdAndSizeId(String productColorId, String sizeId);

}
