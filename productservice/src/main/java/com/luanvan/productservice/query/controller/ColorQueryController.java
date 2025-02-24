package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.query.model.ColorResponseModel;
import com.luanvan.productservice.query.queries.GetAllColorQuery;
import com.luanvan.productservice.query.queries.GetColorDetailQuery;
import com.luanvan.productservice.repository.CategoryRepository;
import com.luanvan.productservice.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
            @RequestParam(defaultValue = "") List<String> sorts) {

        ArrayList<String> sortOrder = new ArrayList<>(sorts);

        GetAllColorQuery query = new GetAllColorQuery(pageNumber, pageSize, sortOrder);

        List<ColorResponseModel> response;
        try {
            response = queryGateway.query(query, ResponseTypes.multipleInstancesOf(ColorResponseModel.class)).join();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        Page<ColorResponseModel> pageResponse = new PageImpl<>(response, PageRequest.of(pageNumber, pageSize), response.size());

        return ApiResponse.<Page<ColorResponseModel>>builder()
                .data(pageResponse)
                .build();
    }

    @GetMapping("/{colorId}")
    public ApiResponse<ColorResponseModel> getDetail(@PathVariable String colorId) {

        if(!colorRepository.existsById(colorId)) {
            throw new AppException(ErrorCode.COLOR_NOT_EXISTED);
        }

        GetColorDetailQuery query = new GetColorDetailQuery(colorId);

        ColorResponseModel response = queryGateway.query(query, ResponseTypes.instanceOf(ColorResponseModel.class)).join();

        return ApiResponse.<ColorResponseModel>builder()
                .data(response)
                .build();
    }
}
