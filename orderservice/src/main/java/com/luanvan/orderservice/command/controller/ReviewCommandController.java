package com.luanvan.orderservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.orderservice.command.model.ReviewCreateModel;
import com.luanvan.orderservice.command.service.ReviewCommandService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewCommandController {
    @Autowired
    private ReviewCommandService reviewCommandService;

    @PostMapping
    public ApiResponse<?> create(@Valid @RequestBody ReviewCreateModel model){
        var response = reviewCommandService.create(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }
}
