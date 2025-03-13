package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.response.PromotionResponseModel;
import com.luanvan.productservice.query.model.PagePromotionResponse;
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
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionQueryController {
    private final QueryGateway queryGateway;

    @GetMapping
    public ApiResponse<Page<PromotionResponseModel>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String sorts) {

        GetAllPromotionQuery query = new GetAllPromotionQuery(pageNumber, pageSize, sorts);

        PagePromotionResponse response = queryGateway.query(query, ResponseTypes.instanceOf(PagePromotionResponse.class)).join();
        Page<PromotionResponseModel> pageResponse = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());

        return ApiResponse.<Page<PromotionResponseModel>>builder()
                .data(pageResponse)
                .build();
    }
}
