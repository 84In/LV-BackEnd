package com.luanvan.orderservice.command.service;


import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.commonservice.queries.GetUserDetailQuery;
import com.luanvan.orderservice.command.command.*;
import com.luanvan.orderservice.command.model.ReviewCreateModel;
import com.luanvan.orderservice.repository.OrderDetailRepository;
import com.luanvan.orderservice.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
public class ReviewCommandService {
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public HashMap<?, ?> create(ReviewCreateModel model) {
        log.info("Create review command service userId: {}", model.getUserId());
        GetUserDetailQuery getUserDetailQuery = new GetUserDetailQuery(model.getUserId());
        var user = queryGateway.query(getUserDetailQuery, ResponseTypes.instanceOf(UserResponseModel.class))
                .exceptionally((ex) -> {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                })
                .join();
        GetProductQuery getProductQuery = new GetProductQuery(model.getProductId());
        var product = queryGateway.query(getProductQuery, ResponseTypes.instanceOf(ProductResponseModel.class))
                .exceptionally((ex) -> {
                    throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
                })
                .join();
        var orderDetail = orderDetailRepository.findById(model.getOrderDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));
        if(!orderDetail.getProductId().equals(model.getProductId())) {
            throw new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED);
        }
        CreateReviewCommand command = CreateReviewCommand.builder()
                .id(UUID.randomUUID().toString())
                .rating(model.getRating())
                .comment(model.getComment())
                .userId(model.getUserId())
                .productId(model.getProductId())
                .orderDetailId(model.getOrderDetailId())
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }
}
