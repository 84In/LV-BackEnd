package com.luanvan.productservice.command.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.productservice.command.model.CategoryChangeStatusModel;
import com.luanvan.productservice.command.model.ColorChangeStatusModel;
import com.luanvan.productservice.command.model.ColorCreateModel;
import com.luanvan.productservice.command.model.ColorUpdateModel;
import com.luanvan.productservice.command.service.ColorCommandService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/colors")
public class ColorCommandController {
    @Autowired
    private  CommandGateway commandGateway;
    @Autowired
    private ColorCommandService colorCommandService;

    @PostMapping
    public ApiResponse<?> create(@RequestBody ColorCreateModel model) {
        var response = colorCommandService.save(model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/{colorId}")
    public ApiResponse<?> update(@PathVariable String colorId, @RequestBody ColorUpdateModel model) {
        var response = colorCommandService.update(colorId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @PutMapping("/changeStatus/{colorId}")
    public ApiResponse<?> changeStatus(@PathVariable String colorId, @RequestBody ColorChangeStatusModel model) {
        var response = colorCommandService.changeStatus(colorId, model);
        return ApiResponse.builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/{colorId}")
    public ApiResponse<?> delete(@PathVariable String colorId) {
        var response = colorCommandService.delete(colorId);
        return ApiResponse.builder()
                .build();
    }

}
