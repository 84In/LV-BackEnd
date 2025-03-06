package com.luanvan.userservice.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.userservice.service.WardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wards")
@RequiredArgsConstructor
public class WardController {

    private final WardService wardService;

    @GetMapping
    public ApiResponse<?> getWards() {
        return ApiResponse.builder()
                .data(wardService.getWards())
                .build();
    }

    @GetMapping("/{wardId}")
    public ApiResponse<?> getWard(@PathVariable Integer wardId) {
        return ApiResponse.builder()
                .data(wardService.getWard(wardId))
                .build();
    }

    @GetMapping("/district-{districtId}")
    public ApiResponse<?> getWardDistrict(@PathVariable Integer districtId) {
        return ApiResponse.builder()
                .data(wardService.getWardsByDistrictId(districtId))
                .build();
    }
}
