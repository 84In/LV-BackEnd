package com.luanvan.searchservice.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.queries.GetAllProductWithFilterQuery;
import com.luanvan.searchservice.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
public class ProductSearchController {
    @Autowired
    private ProductSearchService productSearchService;

    @GetMapping("/searchProductsWithFilter")
    public ApiResponse<Page<ProductResponseModel>> searchProductsWithFilter(@ModelAttribute GetAllProductWithFilterQuery queryParams) {
        var response = productSearchService.searchProductsWithFilter(queryParams);
        return ApiResponse.<Page<ProductResponseModel>>builder()
                .data(response)
                .build();
    }
}
