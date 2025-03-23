package com.luanvan.productservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.productservice.command.model.SizeChangeStatusModel;
import com.luanvan.productservice.command.model.SizeCreateModel;
import com.luanvan.productservice.command.model.SizeUpdateModel;
import com.luanvan.productservice.command.service.SizeCommandService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/sizes")
public class SizeCommandController {
    @Autowired
    private SizeCommandService sizeCommandService;

    @PostMapping
    public ApiResponse<?> create(@RequestBody SizeCreateModel model) {
        log.info(model.toString());
        var response = sizeCommandService.save(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/{sizeId}")
    public ApiResponse<?> update(@PathVariable String sizeId, @RequestBody SizeUpdateModel model) {
        var response = sizeCommandService.update(sizeId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/changeStatus/{sizeId}")
    public ApiResponse<?> changeStatus(@PathVariable String sizeId, @RequestBody SizeChangeStatusModel model) {
        var response = sizeCommandService.changeStatus(sizeId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/{sizeId}")
    public ApiResponse<?> delete(@PathVariable String sizeId) {
        var response = sizeCommandService.delete(sizeId);
        return ApiResponse.builder()
                .build();
    }

}
