package com.luanvan.searchservice.controller;

import com.luanvan.commonservice.queries.GetAllProductWithFilterQuery;
import com.luanvan.searchservice.entity.ProductDocument;
import com.luanvan.searchservice.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class ProductSearchController {
    private final ProductSearchService productSearchService;

    @GetMapping("/searchProductsWithFilter")
    public Page<ProductDocument> searchProductsWithFilter(@ModelAttribute GetAllProductWithFilterQuery queryParams) {
        return productSearchService.searchProductsWithFilter(queryParams);
    }
}
