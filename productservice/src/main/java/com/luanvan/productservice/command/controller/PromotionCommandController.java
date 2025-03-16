package com.luanvan.productservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.productservice.command.model.PromotionChangeStatusModel;
import com.luanvan.productservice.command.model.PromotionCreateModel;
import com.luanvan.productservice.command.model.PromotionUpdateModel;
import com.luanvan.productservice.command.service.PromotionCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionCommandController {

    @Autowired
    private PromotionCommandService promotionCommandService;

    @PostMapping
    public ApiResponse<?> create(@RequestBody PromotionCreateModel model) {
        var response = promotionCommandService.save(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/{promotionId}")
    public ApiResponse<?> update(@PathVariable String promotionId, @RequestBody PromotionUpdateModel model) {
        var response = promotionCommandService.update(promotionId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/changeStatus/{promotionId}")
    public ApiResponse<?> changeStatus(@PathVariable String promotionId, @RequestBody PromotionChangeStatusModel model) {
        var response = promotionCommandService.changeStatus(promotionId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/{promotionId}")
    public ApiResponse<?> delete(@PathVariable String promotionId) {
        var response = promotionCommandService.delete(promotionId);
        return ApiResponse.builder()
                .build();
    }

}
