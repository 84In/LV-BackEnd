package com.luanvan.orderservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.orderservice.command.model.OrderCreateModel;
import com.luanvan.orderservice.command.service.OrderCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
public class OrderCommandController {
    @Autowired
    private OrderCommandService orderCommandService;

    @PostMapping("/cash")
    public ApiResponse<?> createOrderWithCash(OrderCreateModel model){
        var response = orderCommandService.createWithCash(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

}
