package com.luanvan.productservice.query.projection;

import com.luanvan.productservice.query.model.CategoryResponseModel;
import com.luanvan.productservice.query.model.SizeResponseModel;
import com.luanvan.productservice.query.queries.GetAllCategoryQuery;
import com.luanvan.productservice.query.queries.GetAllSizeQuery;
import com.luanvan.productservice.repository.CategoryRepository;
import com.luanvan.productservice.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SizeProjection {
    private final SizeRepository sizeRepository;

    @QueryHandler
    public List<SizeResponseModel> handle(GetAllSizeQuery query) {
        // Tạo PageRequest từ các tham số
        Pageable pageable = PageRequest.of(
                query.getPage(),
                query.getSize(),
                Sort.by(query.getSortDirection(), query.getSortBy())
        );

        var sizePage = sizeRepository.findAll(pageable);

        return sizePage.getContent().stream()
                .map(size -> {
                    SizeResponseModel response = new SizeResponseModel();
                    BeanUtils.copyProperties(size, response);
                    return response;
                })
                .collect(Collectors.toList());
    }

}
