package com.luanvan.userservice.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.userservice.entity.District;
import com.luanvan.userservice.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/districts")
@RequiredArgsConstructor
public class DistrictController {

    public final DistrictService districtService;

    @GetMapping()
    public ApiResponse<?> getDistricts() {
        return ApiResponse.builder()
                .data(districtService.getDistricts())
                .build();
    }

    @GetMapping("/{districtId}")
    public ApiResponse<?> getDistrictById(@PathVariable Integer districtId) {
        return ApiResponse.builder()
                .data(districtService.getDistrictById(districtId))
                .build();
    }

    @GetMapping("/province-{provinceId}")
    public ApiResponse<?> getProvinceById(@PathVariable Integer provinceId) {
        return ApiResponse.builder()
                .data(districtService.getDistrictByProvinceId(provinceId))
                .build();
    }
}
