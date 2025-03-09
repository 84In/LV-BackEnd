package com.luanvan.orderservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.command.RollBackStockProductCommand;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.orderservice.command.event.*;
import com.luanvan.orderservice.entity.*;
import com.luanvan.orderservice.repository.OrderDetailRepository;
import com.luanvan.orderservice.repository.OrderRepository;
import com.luanvan.orderservice.repository.OrderStatusRepository;
import com.luanvan.orderservice.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewEventHandler {

    private final QueryGateway queryGateway;
    private final CommandGateway commandGateway;
    private final OrderDetailRepository orderDetailRepository;
    private final ReviewRepository reviewRepository;

    @EventHandler
    @Transactional
    public void handle(ReviewCreateEvent event) {
        log.info("ReviewCreateEvent: " + event.getId());
        OrderDetail orderDetail = orderDetailRepository.findById(event.getOrderDetailId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_DETAIL_NOT_EXISTED));
        orderDetail.setIsReview(true);
        var finalOrderDetail = orderDetailRepository.save(orderDetail);
        Review review = Review.builder()
                .id(event.getId())
                .rating(event.getRating())
                .comment(event.getComment())
                .userId(event.getUserId())
                .productId(event.getProductId())
                .orderDetail(finalOrderDetail)
                .build();
        reviewRepository.save(review);
    }
}
