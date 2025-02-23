package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.productservice.query.model.ColorResponseModel;
import com.luanvan.productservice.query.queries.GetAllColorQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/colors")
@RequiredArgsConstructor
public class ColorQueryController {
    private final QueryGateway queryGateway;

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
}
