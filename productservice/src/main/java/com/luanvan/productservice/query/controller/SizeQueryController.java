package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.query.model.CategoryResponseModel;
import com.luanvan.productservice.query.model.SizeResponseModel;
import com.luanvan.productservice.query.queries.GetAllCategoryQuery;
import com.luanvan.productservice.query.queries.GetAllSizeQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sizes")
@RequiredArgsConstructor
public class SizeQueryController {
    private final QueryGateway queryGateway;

    @GetMapping
    public ApiResponse<Page<SizeResponseModel>> getAll(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String sortBy = "createdAt";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().get().findFirst().orElse(null);
            if (order != null) {
                sortBy = order.getProperty();
                sortDirection = order.getDirection();
            }
        }
        GetAllSizeQuery query = new GetAllSizeQuery(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortBy,
                sortDirection
        );
        List<SizeResponseModel> response = queryGateway
                .query(query, ResponseTypes.multipleInstancesOf(SizeResponseModel.class))
                .exceptionally((ex) -> {throw new AppException(ErrorCode.QUERY_ERROR);})
                .join();
        Page<SizeResponseModel> pageResponse = new PageImpl<>(response, pageable, response.size());
        return ApiResponse.<Page<SizeResponseModel>>builder()
                .data(pageResponse)
                .build();
    }
}
