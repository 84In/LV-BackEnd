package com.luanvan.orderservice.query.model;

import com.luanvan.commonservice.model.response.ProductResponseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRatingResponse {
    private Double rating;
    private Integer totalReviews;
    private ProductResponseModel product;
}
