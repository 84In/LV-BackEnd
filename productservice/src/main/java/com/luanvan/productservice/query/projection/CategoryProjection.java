package com.luanvan.productservice.query.projection;

import com.luanvan.commonservice.utils.SearchParamsUtils;
import com.luanvan.productservice.query.model.CategoryResponseModel;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryProjection {
    private final CategoryRepository categoryRepository;

    @QueryHandler
    public List<CategoryResponseModel> handle(GetAllCategoryQuery query) {
        // Tạo PageRequest từ các tham số
        Sort sort = SearchParamsUtils.getSortParams(query.getSortOrder());

        Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);

        var categoryPage = categoryRepository.findAll(pageable);

        return categoryPage.getContent().stream()
                .map(category -> {
                    CategoryResponseModel response = new CategoryResponseModel();
                    BeanUtils.copyProperties(category, response);
                    return response;
                })
                .collect(Collectors.toList());
    }

}
