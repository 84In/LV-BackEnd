package com.luanvan.orderservice.query.controller;


import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.orderservice.query.model.*;
import com.luanvan.orderservice.query.queries.*;
import com.luanvan.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewQueryController {
    @Autowired
    private QueryGateway queryGateway;

    @GetMapping("/getAll")
    public ApiResponse<Page<ReviewResponseModel>> getAllReview(
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String sorts
    ) {
        GetAllReviewQuery query = new GetAllReviewQuery(rating, pageNumber, pageSize, sorts);
        PageReviewResponse response = queryGateway.query(query, ResponseTypes.instanceOf(PageReviewResponse.class)).join();
        Page<ReviewResponseModel> pageResponse = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());
        return ApiResponse.<Page<ReviewResponseModel>>builder()
                .data(pageResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<PageReviewResponseModel> getProductAllReview(
            @RequestParam(defaultValue = "", required = false) String productId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String sorts
    ) {
        GetProductReviewQuery query = new GetProductReviewQuery(productId, rating, pageNumber, pageSize, sorts);
        PageReviewResponse response = queryGateway.query(query, ResponseTypes.instanceOf(PageReviewResponse.class))
                .exceptionally((ex) -> {
                    throw new AppException(ErrorCode.REVIEW_NOT_EXISTED);
                })
                .join();
        Page<ReviewResponseModel> page = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());
        var pageResponse = PageReviewResponseModel.builder()
                .averageRating(response.getAverageRating())
                .totalReviews(response.getTotalElements())
                .reviews(page)
                .build();
        return ApiResponse.<PageReviewResponseModel>builder()
                .data(pageResponse)
                .build();
    }
}
