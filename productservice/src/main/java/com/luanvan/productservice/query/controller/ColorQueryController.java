package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.response.ColorResponseModel;
import com.luanvan.commonservice.queries.GetColorQuery;
import com.luanvan.productservice.query.model.PageColorResponse;
import com.luanvan.productservice.query.queries.GetAllColorQuery;
import com.luanvan.productservice.query.service.TotalPageColor;
import com.luanvan.productservice.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/colors")
@RequiredArgsConstructor
public class ColorQueryController {
    private final QueryGateway queryGateway;
    private final ColorRepository colorRepository;

    @GetMapping
    public ApiResponse<Page<ColorResponseModel>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String sorts) {

        GetAllColorQuery query = new GetAllColorQuery(pageNumber, pageSize, sorts);

        PageColorResponse response = queryGateway.query(query, ResponseTypes.instanceOf(PageColorResponse.class)).join();
        Page<ColorResponseModel> pageResponse = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());

        return ApiResponse.<Page<ColorResponseModel>>builder()
                .data(pageResponse)
                .build();
    }

    @GetMapping("/{colorId}")
    public ApiResponse<ColorResponseModel> getDetail(@PathVariable String colorId) {

        if (!colorRepository.existsById(colorId)) {
            throw new AppException(ErrorCode.COLOR_NOT_EXISTED);
        }

        GetColorQuery query = new GetColorQuery(colorId);

        ColorResponseModel response = queryGateway.query(query, ResponseTypes.instanceOf(ColorResponseModel.class)).join();

        return ApiResponse.<ColorResponseModel>builder()
                .data(response)
                .build();
    }
}
