package com.luanvan.userservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.userservice.command.model.CartCreateModel;
import com.luanvan.userservice.command.model.CartUpdateModel;
import com.luanvan.userservice.command.service.CartCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/carts")
public class CartCommandController {

    @Autowired
    private CartCommandService cartCommandService;

    @PostMapping
    public ApiResponse<?> create(@RequestBody CartCreateModel model) {

        var response = cartCommandService.create(model);
        return ApiResponse.builder()
                .code(0)
                .data(response)
                .build();
    }

    @PutMapping
    public ApiResponse<?> update(@RequestBody CartUpdateModel model) {

        var response = cartCommandService.update(model);
        return ApiResponse.builder()
                .code(0)
                .data(response)
                .build();
    }
}
