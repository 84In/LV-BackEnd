package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.productservice.query.model.CategoryResponseModel;
import com.luanvan.productservice.query.model.PageCategoryResponse;
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
            @RequestParam(defaultValue = "") String sorts) {

        GetAllCategoryQuery query = new GetAllCategoryQuery(pageNumber, pageSize, sorts);

        PageCategoryResponse response= queryGateway.query(query, ResponseTypes.instanceOf(PageCategoryResponse.class)).join();
        Page<CategoryResponseModel> pageResponse = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());

        return ApiResponse.<Page<CategoryResponseModel>>builder()
                .data(pageResponse)
                .build();
    }
}
