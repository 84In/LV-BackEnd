package com.luanvan.productservice.command.controller;

import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.command.model.PromotionCreateModel;
import com.luanvan.productservice.command.model.PromotionUpdateModel;
import com.luanvan.productservice.command.model.SizeCreateModel;
import com.luanvan.productservice.command.model.SizeUpdateModel;
import com.luanvan.productservice.command.service.PromotionCommandService;
import com.luanvan.productservice.command.service.SizeCommandService;
import com.luanvan.productservice.repository.PromotionRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/promotions")
public class PromotionCommandController {
    @Autowired
    private  CommandGateway commandGateway;
    @Autowired
    private PromotionCommandService promotionCommandService;

    @PostMapping
    public ApiResponse<?> save(@RequestBody PromotionCreateModel model) {
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

    @DeleteMapping("/{promotionId}")
    public ApiResponse<?> delete(@PathVariable String promotionId) {
        var response = promotionCommandService.delete(promotionId);
        return ApiResponse.builder()
                .build();
    }

}
