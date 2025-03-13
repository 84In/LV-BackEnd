package com.luanvan.productservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.utils.SearchParamsUtils;
import com.luanvan.commonservice.model.response.SizeResponseModel;
import com.luanvan.productservice.query.model.PageSizeResponse;
import com.luanvan.productservice.query.queries.GetAllSizeQuery;
import com.luanvan.commonservice.queries.GetSizeQuery;
import com.luanvan.productservice.repository.SizeRepository;
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
public class SizeProjection {
    private final SizeRepository sizeRepository;

    @QueryHandler
    public PageSizeResponse handle(GetAllSizeQuery query) {
        // Tạo PageRequest từ các tham số
        Sort sort = SearchParamsUtils.getSortParams(query.getSortOrder());

        Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);

        var sizePage = sizeRepository.findAll(pageable);

        var responsePage = sizePage.getContent().stream()
                .map(size -> {
                    SizeResponseModel response = new SizeResponseModel();
                    BeanUtils.copyProperties(size, response);
                    return response;
                })
                .collect(Collectors.toList());
        return PageSizeResponse.builder()
                .content(new ArrayList<>(responsePage))
                .pageNumber(sizePage.getNumber())
                .pageSize(sizePage.getSize())
                .totalElements(sizePage.getTotalElements())
                .totalPages(sizePage.getTotalPages())
                .build();
    }

    @QueryHandler
    public SizeResponseModel handle(GetSizeQuery query) {
        var sizeDetail = sizeRepository.findById(query.getSizeId()).orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));
        SizeResponseModel response = new SizeResponseModel();
        BeanUtils.copyProperties(sizeDetail, response);
        return response;
    }

}
