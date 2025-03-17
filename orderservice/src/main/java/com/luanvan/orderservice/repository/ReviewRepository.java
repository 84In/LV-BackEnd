package com.luanvan.orderservice.repository;

import com.luanvan.orderservice.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    Page<Review> findAll(Specification<Review> specification, Pageable pageable);

    @Query("select round(avg(r.rating), 1) from Review r where r.productId = :productId")
    Double findAverageRatingByProductId(@Param("productId") String productId);

    Long countByProductId(String productId);

}
