package com.luanvan.searchservice.mapper;

import com.luanvan.commonservice.event.ProductCreateEvent;
import com.luanvan.commonservice.model.response.CategoryResponseModel;
import com.luanvan.commonservice.model.response.ColorResponseModel;
import com.luanvan.commonservice.model.response.PromotionResponseModel;
import com.luanvan.commonservice.model.response.SizeResponseModel;
import com.luanvan.commonservice.queries.GetCategoryQuery;
import com.luanvan.commonservice.queries.GetColorQuery;
import com.luanvan.commonservice.queries.GetPromotionQuery;
import com.luanvan.commonservice.queries.GetSizeQuery;
import com.luanvan.searchservice.entity.ProductDocument;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductDocumentMapper {
    @Autowired
    private QueryGateway queryGateway;

    // Build ProductCreateEvent Mapper
    public ProductDocument buildProductDocument(ProductCreateEvent event) {
        CategoryResponseModel categoryResponse = queryGateway.query(
                new GetCategoryQuery(event.getCategoryId()), ResponseTypes.instanceOf(CategoryResponseModel.class)
        ).join();

        return ProductDocument.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .images(event.getImages())
                .isActive(event.getIsActive())
                .category(ProductDocument.CategoryDocument.builder()
                        .id(categoryResponse.getId())
                        .name(categoryResponse.getName())
                        .codeName(categoryResponse.getCodeName())
                        .images(categoryResponse.getImages())
                        .description(categoryResponse.getDescription())
                        .isActive(categoryResponse.getIsActive())
                        .build())
                .productColors(event.getProductColors().stream()
                        .map(productColor -> buildProductColorDocument(productColor))
                        .collect(Collectors.toList()))
                .build();
    }

    public ProductDocument.ProductColorDocument buildProductColorDocument(ProductCreateEvent.ProductColorCreateEvent productColor) {
        var finalPromotion = productColor.getPromotions().stream()
                .map(promoId -> {
                    var promotionResponse = queryGateway.query(new GetPromotionQuery(promoId), ResponseTypes.instanceOf(PromotionResponseModel.class)).join();
                    return ProductDocument.ProductColorDocument.PromotionDocument.builder()
                            .id(promotionResponse.getId())
                            .name(promotionResponse.getName())
                            .codeName(promotionResponse.getCodeName())
                            .discountPercentage(promotionResponse.getDiscountPercentage())
                            .isActive(promotionResponse.getIsActive())
                            .startDate(promotionResponse.getStartDate())
                            .endDate(promotionResponse.getEndDate())
                            .build();
                }).collect(Collectors.toList());

        var colorResponse = queryGateway.query(new GetColorQuery(productColor.getColorId()), ResponseTypes.instanceOf(ColorResponseModel.class)).join();

        return ProductDocument.ProductColorDocument.builder()
                .id(productColor.getId())
                .price(productColor.getPrice())
                .isActive(productColor.getIsActive())
                .color(ProductDocument.ProductColorDocument.ColorDocument.builder()
                        .id(colorResponse.getId())
                        .name(colorResponse.getName())
                        .codeName(colorResponse.getCodeName())
                        .colorCode(colorResponse.getColorCode())
                        .description(colorResponse.getDescription())
                        .isActive(colorResponse.getIsActive())
                        .build())
                .promotions(finalPromotion)
                .productVariants(productColor.getProductVariants().stream()
                        .map(this::buildProductVariantDocument)
                        .collect(Collectors.toList()))
                .build();
    }

    public ProductDocument.ProductColorDocument.ProductVariantDocument buildProductVariantDocument(ProductCreateEvent.ProductVariantCreateEvent productVariant) {
        var sizeResponse = queryGateway.query(new GetSizeQuery(productVariant.getSizeId()), ResponseTypes.instanceOf(SizeResponseModel.class)).join();

        return ProductDocument.ProductColorDocument.ProductVariantDocument.builder()
                .id(productVariant.getId())
                .stock(productVariant.getStock())
                .sold(productVariant.getSold())
                .isActive(productVariant.getIsActive())
                .size(ProductDocument.ProductColorDocument.ProductVariantDocument.SizeDocument.builder()
                        .id(sizeResponse.getId())
                        .name(sizeResponse.getName())
                        .codeName(sizeResponse.getCodeName())
                        .isActive(sizeResponse.getIsActive())
                        .build())
                .build();
    }
}
