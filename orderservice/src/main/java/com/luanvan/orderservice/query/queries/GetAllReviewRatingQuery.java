package com.luanvan.orderservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllReviewRatingQuery {
    private int pageNumber;
    private int pageSize;
    private String sortBy;
    private String order;
}
