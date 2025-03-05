package com.luanvan.orderservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.orderservice.command.model.OrderChangeStatusModel;
import com.luanvan.orderservice.command.model.OrderCreateModel;
import com.luanvan.orderservice.command.service.OrderCommandService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
public class OrderCommandController {
    @Autowired
    private OrderCommandService orderCommandService;

    @PostMapping("/cash")
    public ApiResponse<?> createOrderWithCash(@RequestBody OrderCreateModel model){
        var response = orderCommandService.createWithCash(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PostMapping("/vnpay")
    public ApiResponse<?> createOrderWithVNPay(HttpServletRequest request, @RequestBody OrderCreateModel model){
        var response = orderCommandService.createWithVNPay(request, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @GetMapping("/vnpay-callback")
    public void vnPayCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        orderCommandService.vnPayCallBack(request, response);
    }

    @PutMapping("/changeStatus")
    public ApiResponse<?> changeOrderStatus(@RequestBody OrderChangeStatusModel model){
        var response = orderCommandService.changeOrderStatus(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/{orderId}")
    public ApiResponse<?> cancelOrder(HttpServletRequest request, @PathVariable String orderId){
        var response = orderCommandService.cancelOrder(request, orderId);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

}
