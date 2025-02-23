package com.luanvan.orderservice.repository;

import com.luanvan.orderservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

//    Page<Review> findByProductId(Pageable pageable, String productId);
//
//    @Query("SELECT ROUND(AVG(r.rating), 1) FROM Review r WHERE r.product.id = :productId")
//    Double findAverageRatingByProductId(@Param("productId") String productId);
}
