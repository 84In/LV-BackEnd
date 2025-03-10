package com.luanvan.productservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.productservice.command.model.CategoryChangeStatusModel;
import com.luanvan.productservice.command.model.CategoryCreateModel;
import com.luanvan.productservice.command.model.CategoryUpdateModel;
import com.luanvan.productservice.command.service.CategoryCommandService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryCommandController {
    @Autowired
    private  CommandGateway commandGateway;
    @Autowired
    private  CategoryCommandService categoryCommandService;

    @PostMapping
    public ApiResponse<?> create(@RequestBody CategoryCreateModel model) {
        var response = categoryCommandService.save(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<?> update(@PathVariable String categoryId, @RequestBody CategoryUpdateModel model) {
        var response = categoryCommandService.update(categoryId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/changeStatus/{categoryId}")
    public ApiResponse<?> changeStatus(@PathVariable String categoryId, @RequestBody CategoryChangeStatusModel model) {
        var response = categoryCommandService.changeStatus(categoryId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }


    @DeleteMapping("/{categoryId}")
    public ApiResponse<?> delete(@PathVariable String categoryId) {
        var response = categoryCommandService.delete(categoryId);
        return ApiResponse.builder()
                .build();
    }

}
