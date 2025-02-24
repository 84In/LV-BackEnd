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
import com.luanvan.productservice.query.queries.GetColorDetailQuery;
import com.luanvan.productservice.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sizes")
@RequiredArgsConstructor
public class SizeQueryController {
    private final QueryGateway queryGateway;
    private final SizeRepository sizeRepository;

    @GetMapping
    public ApiResponse<Page<SizeResponseModel>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") List<String> sorts) {

        ArrayList<String> sortOrder = new ArrayList<>(sorts);

        GetAllSizeQuery query = new GetAllSizeQuery(pageNumber, pageSize, sortOrder);

        List<SizeResponseModel> response;
        try {
            response = queryGateway.query(query, ResponseTypes.multipleInstancesOf(SizeResponseModel.class)).join();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        Page<SizeResponseModel> pageResponse = new PageImpl<>(response, PageRequest.of(pageNumber, pageSize), response.size());

        return ApiResponse.<Page<SizeResponseModel>>builder()
                .data(pageResponse)
                .build();
    }

    @GetMapping("/{sizeId}")
    public ApiResponse<SizeResponseModel> getDetail(@PathVariable String sizeId) {

        if(!sizeRepository.existsById(sizeId)) {
            throw new AppException(ErrorCode.SIZE_NOT_EXISTED);
        }

        GetSizeDetailQuery query = new GetSizeDetailQuery(sizeId);

        SizeResponseModel response = queryGateway.query(query, ResponseTypes.instanceOf(SizeResponseModel.class)).join();

        return ApiResponse.<SizeResponseModel>builder()
                .data(response)
                .build();
    }
}
