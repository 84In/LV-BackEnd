package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.query.model.CategoryResponseModel;
import com.luanvan.productservice.query.queries.GetAllCategoryQuery;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryQueryController {
    private final QueryGateway queryGateway;

    @GetMapping
    public ApiResponse<Page<CategoryResponseModel>> getAllCategories(
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
        GetAllCategoryQuery query = new GetAllCategoryQuery(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortBy,
                sortDirection
        );
        List<CategoryResponseModel> response = queryGateway
                .query(query, ResponseTypes.multipleInstancesOf(CategoryResponseModel.class))
                .exceptionally((ex) -> {throw new AppException(ErrorCode.QUERY_ERROR);})
                .join();
        // Tạo Page<CategoryResponseModel> từ List<CategoryResponseModel>
        Page<CategoryResponseModel> pageResponse = new PageImpl<>(response, pageable, response.size());
        return ApiResponse.<Page<CategoryResponseModel>>builder()
                .data(pageResponse)
                .build();
    }
}
