package com.luanvan.productservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.*;
import com.luanvan.productservice.query.queries.GetAllProductQuery;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.commonservice.queries.GetAllProductWithFilterQuery;
import com.luanvan.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductQueryController {
    private final QueryGateway queryGateway;
    private final ProductRepository productRepository;

    @GetMapping
    public ApiResponse<Page<AllProductResponseModel>> getAll(
            @RequestParam(defaultValue = "", required = false) String query,
            @RequestParam(defaultValue = "", required = false) String category,
            @RequestParam(defaultValue = "", required = false) String price,
            @RequestParam(defaultValue = "", required = false) String size,
            @RequestParam(defaultValue = "", required = false) String color,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String sortOrder) {
        ;

        GetAllProductQuery queryGetAll = new GetAllProductQuery(query, category, price, size, color, pageNumber, pageSize, sortOrder);

        PageAllProductResponse response = queryGateway.query(queryGetAll, ResponseTypes.instanceOf(PageAllProductResponse.class)).join();
        Page<AllProductResponseModel> pageResponse = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());

        return ApiResponse.<Page<AllProductResponseModel>>builder()
                .data(pageResponse)
                .build();
    }

    @GetMapping("/getAllWithFilter")
    public ApiResponse<Page<ProductResponseModel>> getAllWithFilter(
            @RequestParam(defaultValue = "", required = false) String query,
            @RequestParam(defaultValue = "", required = false) String category,
            @RequestParam(defaultValue = "", required = false) String price,
            @RequestParam(defaultValue = "", required = false) String size,
            @RequestParam(defaultValue = "", required = false) String color,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "") String sortOrder) {
        ;

        GetAllProductWithFilterQuery queryGetAll = new GetAllProductWithFilterQuery(query, category, price, size, color, pageNumber, pageSize, sortOrder);

        PageProductResponse response = queryGateway.query(queryGetAll, ResponseTypes.instanceOf(PageProductResponse.class)).join();
        Page<ProductResponseModel> pageResponse = new PageImpl<>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());

        return ApiResponse.<Page<ProductResponseModel>>builder()
                .data(pageResponse)
                .build();
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseModel> getDetail(@PathVariable("productId") String productId) {

        if(!productRepository.existsById(productId)) throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);

        GetProductQuery query = new GetProductQuery(productId);

        ProductResponseModel response = queryGateway.query(query, ResponseTypes.instanceOf(ProductResponseModel.class)).join();

        return ApiResponse.<ProductResponseModel>builder()
                .data(response)
                .build();
    }
}
