package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.query.model.CategoryResponseModel;
import com.luanvan.productservice.query.queries.GetAllCategoryQuery;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryQueryController {
    private final QueryGateway queryGateway;

    @GetMapping
    public ApiResponse<Page<CategoryResponseModel>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") List<String> sorts) {

        ArrayList<String> sortOrder = new ArrayList<>(sorts);

        GetAllCategoryQuery query = new GetAllCategoryQuery(pageNumber, pageSize, sortOrder);

        List<CategoryResponseModel> response;
        try {
            response = queryGateway.query(query, ResponseTypes.multipleInstancesOf(CategoryResponseModel.class)).join();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        Page<CategoryResponseModel> pageResponse = new PageImpl<>(response, PageRequest.of(pageNumber, pageSize), response.size());

        return ApiResponse.<Page<CategoryResponseModel>>builder()
                .data(pageResponse)
                .build();
    }
}
