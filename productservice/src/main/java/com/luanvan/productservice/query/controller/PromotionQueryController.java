package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.response.PromotionResponseModel;
import com.luanvan.commonservice.queries.GetPromotionQuery;
import com.luanvan.productservice.query.model.PagePromotionResponse;
import com.luanvan.productservice.query.queries.GetAllPromotionQuery;
import com.luanvan.productservice.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionQueryController {
    private final QueryGateway queryGateway;
    private final PromotionRepository promotionRepository;

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

    @GetMapping("/{promotionId}")
    public ApiResponse<PromotionResponseModel> getDetail(@PathVariable String promotionId) {

        if (!promotionRepository.existsById(promotionId)) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXISTED);
        }

        GetPromotionQuery query = new GetPromotionQuery(promotionId);

        PromotionResponseModel response = queryGateway.query(query, ResponseTypes.instanceOf(PromotionResponseModel.class)).join();

        return ApiResponse.<PromotionResponseModel>builder()
                .data(response)
                .build();
    }
}
