package com.luanvan.productservice.repository;

import com.luanvan.productservice.entity.Color;
import com.luanvan.productservice.entity.Product;
import com.luanvan.productservice.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, String> {
    List<ProductColor> findAllByProduct(Product product);
    Optional<ProductColor> findByProductIdAndColorId(String productId, String colorId);

}
