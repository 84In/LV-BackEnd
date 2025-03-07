package com.luanvan.userservice.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.userservice.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/provinces")
@RequiredArgsConstructor
public class ProvinceController {

    private final ProvinceService provinceService;
    @GetMapping
    public ApiResponse<?> getProvinces() {
        return ApiResponse.builder()
                .data(provinceService.getProvinces())
                .build();
    }

    @GetMapping("/{provinceId}")
    public ApiResponse<?> getProvince(@PathVariable Integer provinceId) {
        return ApiResponse.builder()
                .data(provinceService.getProvince(provinceId))
                .build();
    }
}
