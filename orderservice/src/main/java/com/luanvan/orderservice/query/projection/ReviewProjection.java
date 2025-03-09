package com.luanvan.orderservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.commonservice.queries.GetUserDetailQuery;
import com.luanvan.orderservice.entity.OrderDetail;
import com.luanvan.orderservice.entity.Review;
import com.luanvan.orderservice.mapper.OrderMapper;
import com.luanvan.orderservice.query.model.PageReviewResponse;
import com.luanvan.orderservice.query.model.ReviewResponseModel;
import com.luanvan.orderservice.query.queries.*;
import com.luanvan.orderservice.repository.ReviewRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReviewProjection {
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ReviewRepository reviewRepository;

    @QueryHandler
    public PageReviewResponse handle(GetAllReviewQuery queryParams) {
        log.info("Get all review");
        Specification<Review> specification = (root, cq, cb) -> {
            cq.groupBy(root.get("id"));

            List<Predicate> predicates = new ArrayList<>();
            Join<Review, OrderDetail> orderDetailJoin = root.join("orderDetail", JoinType.LEFT);

            // Lọc theo rating (nếu có)
            if (queryParams.getRating() != null) {
                predicates.add(cb.equal(root.get("rating"), queryParams.getRating()));
            }

            // Sắp xếp theo ngày tạo mới nhất
            cq.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(queryParams.getPageNumber(), queryParams.getPageSize());
        var reviewPage = reviewRepository.findAll(specification, pageable);
        var responsePage = reviewPage.getContent().stream().map(this::toReviewResponseModel).collect(Collectors.toList());

        return PageReviewResponse.builder()
                .content(new ArrayList<>(responsePage))
                .pageNumber(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .build();
    }

    @QueryHandler
    public PageReviewResponse handle(GetProductReviewQuery queryParams) {
        log.info("Get product review");
        Specification<Review> specification = (root, cq, cb) -> {
            cq.groupBy(root.get("id"));

            List<Predicate> predicates = new ArrayList<>();
            Join<Review, OrderDetail> orderDetailJoin = root.join("orderDetail", JoinType.LEFT);

            predicates.add(cb.equal(root.get("productId"), queryParams.getProductId()));

            // Lọc theo rating (nếu có)
            if (queryParams.getRating() != null) {
                predicates.add(cb.equal(root.get("rating"), queryParams.getRating()));
            }

            // Sắp xếp theo ngày tạo mới nhất
            cq.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(queryParams.getPageNumber(), queryParams.getPageSize());
        var reviewPage = reviewRepository.findAll(specification, pageable);
        var responsePage = reviewPage.getContent().stream().map(this::toReviewResponseModel).collect(Collectors.toList());

        // Tính số sao trung bình và tổng số lượt đánh giá
        Double averageRating = reviewRepository.findAverageRatingByProductId(queryParams.getProductId());
        Long totalReviews = reviewRepository.countByProductId(queryParams.getProductId());

        return PageReviewResponse.builder()
                .content(new ArrayList<>(responsePage))
                .pageNumber(reviewPage.getNumber())
                .pageSize(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .averageRating(averageRating != null ? averageRating : 0.0)
                .totalReviews(totalReviews)
                .build();
    }

    private ReviewResponseModel toReviewResponseModel(Review review) {
        var user = queryGateway.query(new GetUserDetailQuery(review.getUserId()),
                        ResponseTypes.instanceOf(UserResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                })
                .join();
        var product = queryGateway.query(new GetProductQuery(review.getProductId()),
                        ResponseTypes.instanceOf(ProductResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
                })
                .join();
        return ReviewResponseModel.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .isActive(review.getIsActive())
                .user(user)
                .productId(review.getProductId())
                .orderDetail(orderMapper.mapOrderDetail(review.getOrderDetail(), product))
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
