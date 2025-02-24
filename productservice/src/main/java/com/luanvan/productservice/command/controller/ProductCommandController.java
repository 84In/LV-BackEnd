package com.luanvan.productservice.command.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.command.model.ProductCreateModel;
import com.luanvan.productservice.command.model.ProductUpdateModel;
import com.luanvan.productservice.command.service.ProductCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/products")
public class ProductCommandController {
    @Autowired
    private ProductCommandService productCommandService;

    @PostMapping
    public ApiResponse<?> save(@RequestPart("images") ArrayList<MultipartFile> images,
                               @RequestPart("data") ProductCreateModel model) throws JsonProcessingException {

        var response = productCommandService.save(images, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/{productId}")
    public ApiResponse<?> update(@PathVariable String productId, @RequestBody ProductUpdateModel model) {

        var response = productCommandService.update(productId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }
}
