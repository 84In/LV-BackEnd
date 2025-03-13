package com.luanvan.productservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ColorResponseModel;
import com.luanvan.commonservice.queries.GetColorQuery;
import com.luanvan.commonservice.utils.SearchParamsUtils;
import com.luanvan.productservice.query.model.PageCategoryResponse;
import com.luanvan.productservice.query.model.PageColorResponse;
import com.luanvan.productservice.query.queries.GetAllColorQuery;
import com.luanvan.productservice.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ColorProjection {
    private final ColorRepository colorRepository;

    @QueryHandler
    public PageColorResponse handle(GetAllColorQuery query) {
        Sort sort = SearchParamsUtils.getSortParams(query.getSortOrder());

        Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);

        var colorPage = colorRepository.findAll(pageable);

        var responsePage =  colorPage.getContent().stream()
                .map(color -> {
                    ColorResponseModel response = new ColorResponseModel();
                    BeanUtils.copyProperties(color, response);
                    return response;
                })
                .collect(Collectors.toList());
        return PageColorResponse.builder()
                .content(new ArrayList<>(responsePage))
                .pageNumber(colorPage.getNumber())
                .pageSize(colorPage.getSize())
                .totalElements(colorPage.getTotalElements())
                .totalPages(colorPage.getTotalPages())
                .build();
    }

    @QueryHandler
    public ColorResponseModel handle(GetColorQuery query) {
        var colorDetail = colorRepository.findById(query.getColorId()).orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));
        ColorResponseModel response = new ColorResponseModel();
        BeanUtils.copyProperties(colorDetail, response);
        return response;
    }

}
