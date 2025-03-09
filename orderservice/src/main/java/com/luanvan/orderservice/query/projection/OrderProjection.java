package com.luanvan.orderservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.orderservice.entity.Order;
import com.luanvan.orderservice.entity.OrderDetail;
import com.luanvan.orderservice.entity.OrderStatus;
import com.luanvan.orderservice.mapper.OrderMapper;
import com.luanvan.orderservice.query.model.OrderDetailResponse;
import com.luanvan.orderservice.query.model.OrderResponseModel;
import com.luanvan.orderservice.query.model.PageOrderResponse;
import com.luanvan.orderservice.query.queries.GetAllOrderQuery;
import com.luanvan.orderservice.query.queries.GetOrderQuery;
import com.luanvan.orderservice.query.queries.GetUserOrderQuery;
import com.luanvan.orderservice.repository.OrderRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderProjection {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private OrderMapper orderMapper;

    @QueryHandler
    public PageOrderResponse handle(GetAllOrderQuery queryParams) {
        log.info("Get all order");
        Specification<Order> specification = (root, cq, cb) -> {
            cq.groupBy(root.get("id"));

            List<Predicate> predicates = new ArrayList<>();
            Join<Order, OrderStatus> orderStatusJoin = root.join("orderStatus", JoinType.LEFT);

            // Nếu trạng thái không phải là all thì lọc trạng thái theo orderStatus
            if (!"all".equalsIgnoreCase(queryParams.getStatus())) {
                predicates.add(cb.equal(orderStatusJoin.get("codeName"), queryParams.getStatus()));
            }

            // Sắp xếp theo ngày tạo mới nhất
            cq.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(queryParams.getPageNumber(), queryParams.getPageSize());
        var orderPage = orderRepository.findAll(specification, pageable);
        var responsePage = orderPage.getContent().stream().map(orderMapper::toOrderResponseModel).collect(Collectors.toList());
        return PageOrderResponse.builder()
                .content(new ArrayList<>(responsePage))
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build();
    }

    @QueryHandler
    public PageOrderResponse handle(GetUserOrderQuery queryParams) {
        log.info("Get order by userId: {}", queryParams.getUserId());
        Specification<Order> specification = (root, cq, cb) -> {
            cq.groupBy(root.get("id"));

            List<Predicate> predicates = new ArrayList<>();
            Join<Order, OrderStatus> orderStatusJoin = root.join("orderStatus", JoinType.LEFT);

            // Tìm kiếm order với userId
            predicates.add(cb.equal(root.get("userId"), queryParams.getUserId()));

            // Nếu trạng thái không phải là all thì lọc trạng thái theo orderStatus
            if (!"all".equalsIgnoreCase(queryParams.getStatus())) {
                predicates.add(cb.equal(orderStatusJoin.get("codeName"), queryParams.getStatus()));
            }

            // Sắp xếp theo ngày tạo mới nhất
            cq.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(queryParams.getPageNumber(), queryParams.getPageSize());
        var orderPage = orderRepository.findAll(specification, pageable);
        var responsePage = orderPage.getContent().stream().map(orderMapper::toOrderResponseModel).collect(Collectors.toList());
        return PageOrderResponse.builder()
                .content(new ArrayList<>(responsePage))
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .build();
    }

    @QueryHandler
    public OrderResponseModel handle(GetOrderQuery query) {
        log.info("Get order by orderId: {}", query.getOrderId());

        var order = orderRepository.findById(query.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        return orderMapper.toOrderResponseModel(order);
    }

}
