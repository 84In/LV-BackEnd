package com.luanvan.productservice.command.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.command.command.DeleteCategoryCommand;
import com.luanvan.productservice.command.command.UpdateCategoryCommand;
import com.luanvan.productservice.command.model.CategoryCreateModel;
import com.luanvan.productservice.command.model.CategoryUpdateModel;
import com.luanvan.productservice.command.service.CategoryCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryCommandController {
    @Autowired
    private  CommandGateway commandGateway;
    @Autowired
    private  CategoryCommandService categoryCommandService;

    @PostMapping
    public ApiResponse<?> saveCategory(@RequestBody CategoryCreateModel model) {
        var response = categoryCommandService.saveCategory(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/{categoryId}")
    public ApiResponse<?> updateCategory(@PathVariable String categoryId, @RequestBody CategoryUpdateModel model) {
        var response = categoryCommandService.updateCategory(categoryId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<?> deleteCategory(@PathVariable String categoryId) {
        var response = categoryCommandService.deleteCategory(categoryId);
        return ApiResponse.builder()
                .build();
    }

}
