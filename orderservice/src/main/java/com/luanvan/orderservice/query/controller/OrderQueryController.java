package com.luanvan.orderservice.query.controller;


import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.orderservice.query.model.OrderResponseModel;
import com.luanvan.orderservice.query.model.PageOrderResponse;
import com.luanvan.orderservice.query.queries.GetAllOrderQuery;
import com.luanvan.orderservice.query.queries.GetOrderQuery;
import com.luanvan.orderservice.query.queries.GetUserOrderQuery;
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
@RequestMapping("/api/v1/orders")
public class OrderQueryController {
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/getAll")
    public ApiResponse<Page<OrderResponseModel>> getAllOrder(
            @RequestParam(defaultValue = "all", required = false) String status,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String sorts
    ) {
        GetAllOrderQuery query = new GetAllOrderQuery(status, pageNumber, pageSize, sorts);
        PageOrderResponse response = queryGateway.query(query, ResponseTypes.instanceOf(PageOrderResponse.class)).join();
        Page<OrderResponseModel> pageResponse = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());
        return ApiResponse.<Page<OrderResponseModel>>builder()
                .data(pageResponse)
                .build();
    }

    @GetMapping
    public ApiResponse<Page<OrderResponseModel>> getUserAllOrder(
            @RequestParam(defaultValue = "", required = false) String userId,
            @RequestParam(defaultValue = "all", required = false) String status,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String sorts
    ) {
        GetUserOrderQuery query = new GetUserOrderQuery(userId, status, pageNumber, pageSize, sorts);
        PageOrderResponse response = queryGateway.query(query, ResponseTypes.instanceOf(PageOrderResponse.class)).join();
        Page<OrderResponseModel> pageResponse = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());
        return ApiResponse.<Page<OrderResponseModel>>builder()
                .data(pageResponse)
                .build();
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponseModel> getOrder(@PathVariable String orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        GetOrderQuery query = new GetOrderQuery(orderId);
        OrderResponseModel response = queryGateway.query(query, ResponseTypes.instanceOf(OrderResponseModel.class)).join();
        return ApiResponse.<OrderResponseModel>builder()
                .data(response)
                .build();
    }

}
