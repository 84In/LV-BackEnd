package com.luanvan.productservice.query.projection;

import com.luanvan.commonservice.utils.SearchParamsUtils;
import com.luanvan.commonservice.model.response.PromotionResponseModel;
import com.luanvan.productservice.query.queries.GetAllPromotionQuery;
import com.luanvan.productservice.repository.PromotionRepository;
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
public class PromotionProjection {
    private final PromotionRepository promotionRepository;

    @QueryHandler
    public List<PromotionResponseModel> handle(GetAllPromotionQuery query) {
        // Tạo PageRequest từ các tham số
        Sort sort = SearchParamsUtils.getSortParams(query.getSortOrder());

        Pageable pageable = PageRequest.of(query.getPageNumber(), query.getPageSize(), sort);

        var promotionPage = promotionRepository.findAll(pageable);

        return promotionPage.getContent().stream()
                .map(promotion -> {
                    PromotionResponseModel response = new PromotionResponseModel();
                    BeanUtils.copyProperties(promotion, response);
                    return response;
                })
                .collect(Collectors.toList());
    }

}
