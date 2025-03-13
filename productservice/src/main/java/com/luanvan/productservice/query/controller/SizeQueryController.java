package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.response.SizeResponseModel;
import com.luanvan.productservice.query.model.PageSizeResponse;
import com.luanvan.productservice.query.queries.GetAllSizeQuery;
import com.luanvan.commonservice.queries.GetSizeQuery;
import com.luanvan.productservice.repository.SizeRepository;
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
@RequestMapping("/api/v1/sizes")
@RequiredArgsConstructor
public class SizeQueryController {
    private final QueryGateway queryGateway;
    private final SizeRepository sizeRepository;

    @GetMapping
    public ApiResponse<Page<SizeResponseModel>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String sorts) {

        GetAllSizeQuery query = new GetAllSizeQuery(pageNumber, pageSize, sorts);

        PageSizeResponse response = queryGateway.query(query, ResponseTypes.instanceOf(PageSizeResponse.class)).join();
        Page<SizeResponseModel> pageResponse = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());

        return ApiResponse.<Page<SizeResponseModel>>builder()
                .data(pageResponse)
                .build();
    }

    @GetMapping("/{sizeId}")
    public ApiResponse<SizeResponseModel> getDetail(@PathVariable String sizeId) {

        if (!sizeRepository.existsById(sizeId)) {
            throw new AppException(ErrorCode.SIZE_NOT_EXISTED);
        }

        GetSizeQuery query = new GetSizeQuery(sizeId);

        SizeResponseModel response = queryGateway.query(query, ResponseTypes.instanceOf(SizeResponseModel.class)).join();

        return ApiResponse.<SizeResponseModel>builder()
                .data(response)
                .build();
    }
}
