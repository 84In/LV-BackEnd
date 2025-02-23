package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.query.model.ProductResponseModel;
import com.luanvan.productservice.query.model.PromotionResponseModel;
import com.luanvan.productservice.query.queries.GetAllProductQuery;
import com.luanvan.productservice.query.queries.GetAllPromotionQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQueryController {
    private final QueryGateway queryGateway;

    @GetMapping
    public ApiResponse<Page<ProductResponseModel>> getAll(
            @RequestParam(defaultValue = "", required = false)  String query,
            @RequestParam(defaultValue = "", required = false)  String category,
            @RequestParam(defaultValue = "", required = false)  ArrayList<String> price,
            @RequestParam(defaultValue = "", required = false)  ArrayList<String> size,
            @RequestParam(defaultValue = "", required = false)  ArrayList<String> color,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") ArrayList<String> sorts) {;

        GetAllProductQuery queryGetAll = new GetAllProductQuery(query, category, price, size, color, pageNumber, pageSize, sorts);

        List<ProductResponseModel> response;
        try {
            response = queryGateway.query(queryGetAll, ResponseTypes.multipleInstancesOf(ProductResponseModel.class)).join();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        Page<ProductResponseModel> pageResponse = new PageImpl<>(response, PageRequest.of(pageNumber, pageSize), response.size());

        return ApiResponse.<Page<ProductResponseModel>>builder()
                .data(pageResponse)
                .build();
    }
}
