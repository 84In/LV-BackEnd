package com.luanvan.productservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.queries.GetCategoryQuery;
import com.luanvan.commonservice.utils.SearchParamsUtils;
import com.luanvan.commonservice.model.response.CategoryResponseModel;
import com.luanvan.productservice.query.model.PageCategoryResponse;
import com.luanvan.productservice.query.queries.GetAllCategoryQuery;
import com.luanvan.productservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryProjection {
    private final CategoryRepository categoryRepository;

    @QueryHandler
    public PageCategoryResponse handle(GetAllCategoryQuery query) {
        // Tạo PageRequest từ các tham số
        Sort sort = SearchParamsUtils.getSortParams(query.getSortOrder());

        Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);

        var categoryPage = categoryRepository.findAll(pageable);

        var responsePage = categoryPage.getContent().stream()
                .map(category -> {
                    CategoryResponseModel response = new CategoryResponseModel();
                    BeanUtils.copyProperties(category, response);
                    return response;
                })
                .collect(Collectors.toList());
        return PageCategoryResponse.builder()
                .content(new ArrayList<>(responsePage))
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .build();
    }

    @QueryHandler
    public CategoryResponseModel handle(GetCategoryQuery query) {
        var colorDetail = categoryRepository.findById(query.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        CategoryResponseModel response = new CategoryResponseModel();
        BeanUtils.copyProperties(colorDetail, response);
        return response;
    }
}
