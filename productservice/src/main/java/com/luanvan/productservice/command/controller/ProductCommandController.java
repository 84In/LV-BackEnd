package com.luanvan.productservice.command.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.productservice.command.model.ProductChangeStatusModel;
import com.luanvan.productservice.command.model.ProductCreateModel;
import com.luanvan.productservice.command.model.ProductUpdateModel;
import com.luanvan.productservice.command.service.ProductCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductCommandController {
    @Autowired
    private ProductCommandService productCommandService;

    @PostMapping
    public ApiResponse<?> create(@RequestPart("images") ArrayList<MultipartFile> images,
                               @RequestPart("data") ProductCreateModel model) throws JsonProcessingException {

        var response = productCommandService.save(images, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PostMapping("/create")
    public ApiResponse<?> create(@RequestBody ProductCreateModel model) throws JsonProcessingException {

        var response = productCommandService.save(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/{productId}")
    public ApiResponse<?> update(@PathVariable String productId, @RequestBody ProductUpdateModel model) {
        log.info(productId);
        log.info(model.toString());
        var response = productCommandService.update(productId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }
    @PutMapping("changeStatus/{productId}")
    public ApiResponse<?> changeStatus(@PathVariable String productId, @RequestBody ProductChangeStatusModel model) {

        var response = productCommandService.changeStatus(productId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }
}
