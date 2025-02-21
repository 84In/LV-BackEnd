package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.query.model.CategoryResponseModel;
import com.luanvan.productservice.query.model.ColorResponseModel;
import com.luanvan.productservice.query.model.SizeResponseModel;
import com.luanvan.productservice.query.queries.GetAllCategoryQuery;
import com.luanvan.productservice.query.queries.GetAllColorQuery;
import com.luanvan.productservice.query.queries.GetAllSizeQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sizes")
@RequiredArgsConstructor
public class SizeQueryController {
    private final QueryGateway queryGateway;

    @GetMapping
    public ApiResponse<Page<SizeResponseModel>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") List<String> sorts) {

        ArrayList<String> sortOrder = new ArrayList<>(sorts);

        GetAllSizeQuery query = new GetAllSizeQuery(page, size, sortOrder);

        List<SizeResponseModel> response;
        try {
            response = queryGateway.query(query, ResponseTypes.multipleInstancesOf(SizeResponseModel.class)).join();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        Page<SizeResponseModel> pageResponse = new PageImpl<>(response, PageRequest.of(page, size), response.size());

        return ApiResponse.<Page<SizeResponseModel>>builder()
                .data(pageResponse)
                .build();
    }
}
