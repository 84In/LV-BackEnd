package com.luanvan.searchservice.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.event.CategoryChangeStatusEvent;
import com.luanvan.commonservice.event.CategoryUpdateEvent;
import com.luanvan.commonservice.event.ColorChangeStatusEvent;
import com.luanvan.commonservice.event.ColorUpdateEvent;
import com.luanvan.commonservice.event.ProductCallBackUploadImagesEvent;
import com.luanvan.commonservice.event.ProductChangeStatusEvent;
import com.luanvan.commonservice.event.ProductCreateEvent;
import com.luanvan.commonservice.event.ProductUpdateEvent;
import com.luanvan.commonservice.event.ProductUpdateStockEvent;
import com.luanvan.commonservice.event.PromotionChangeStatusEvent;
import com.luanvan.commonservice.event.PromotionUpdateEvent;
import com.luanvan.commonservice.event.SizeChangeStatusEvent;
import com.luanvan.commonservice.event.SizeUpdateEvent;
import com.luanvan.commonservice.model.response.CategoryResponseModel;
import com.luanvan.commonservice.model.response.ColorResponseModel;
import com.luanvan.commonservice.model.response.PromotionResponseModel;
import com.luanvan.commonservice.model.response.SizeResponseModel;
import com.luanvan.commonservice.queries.GetCategoryQuery;
import com.luanvan.commonservice.queries.GetColorQuery;
import com.luanvan.commonservice.queries.GetPromotionQuery;
import com.luanvan.commonservice.queries.GetSizeQuery;
import com.luanvan.searchservice.entity.ProductDocument;
import com.luanvan.searchservice.mapper.ProductDocumentMapper;
import com.luanvan.searchservice.repository.ProductSearchRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProductSearchEventHandler {
    @Autowired
    private ProductSearchRepository productSearchRepository;
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private ProductDocumentMapper productDocumentMapper;

    @EventHandler
    public void on(ProductCreateEvent event) {
        log.info("Handling ProductCreateEvent for productId: {}", event.getId());

        ProductDocument productDocument = productDocumentMapper.buildProductDocument(event);
        productDocument.updateTimestamps();
        productSearchRepository.save(productDocument);
    }

    @EventHandler
    public void on(ProductUpdateEvent event) {
        log.info("Handling ProductUpdateEvent for productId: {}", event.getId());

        var product = productSearchRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        CategoryResponseModel categoryResponse = queryGateway.query(
                new GetCategoryQuery(event.getCategoryId()), ResponseTypes.instanceOf(CategoryResponseModel.class)
        ).join();

        product.setName(event.getName());
        product.setDescription(event.getDescription());
        product.setImages(event.getImages());
        product.setIsActive(event.getIsActive());
        product.setCategory(ProductDocument.CategoryDocument.builder()
                .id(categoryResponse.getId())
                .name(categoryResponse.getName())
                .codeName(categoryResponse.getCodeName())
                .images(categoryResponse.getImages())
                .description(categoryResponse.getDescription())
                .isActive(categoryResponse.getIsActive())
                .build());
        product.setProductColors(event.getProductColors().stream()
                .map(productColor -> {
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
                                    .map(productVariant -> {
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
                                    })
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList()));

        product.updateTimestamps();
        productSearchRepository.save(product);
    }

    @EventHandler
    public void on(ProductChangeStatusEvent event) {
        log.info("Handling ProductChangeStatusEvent for productId: {}", event.getId());

        var product = productSearchRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        product.setIsActive(event.getIsActive());
        product.updateTimestamps();
        productSearchRepository.save(product);
    }

    @EventHandler
    public void on(ProductUpdateStockEvent event) {
        log.info("Handling ProductUpdateStockEvent for productId: {}", event.getId());


        var product = productSearchRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        for (var color : product.getProductColors()) {
            if (color.getColor().getId().equals(event.getColorId())) {
                for (var variant : color.getProductVariants()) {
                    if (variant.getSize().getId().equals(event.getSizeId())) {
                        var updatedStock = variant.getStock() - event.getQuantity();
                        if (updatedStock < 0L) {
                            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
                        }
                        variant.setStock(updatedStock);
                        variant.setSold(variant.getSold() + event.getQuantity());
                    }
                }
            }
        }
        product.updateTimestamps();
        productSearchRepository.save(product);
    }

    @EventHandler
    public void uploadedProductImages(ProductCallBackUploadImagesEvent event) {

        log.info("Received product images upload event for productId: {} with URLs: {}", event.getProductId(), String.join(",", event.getImageUrls()));

        try {
            ProductDocument product = productSearchRepository.findById(event.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            product.setImages(String.join(",", event.getImageUrls()));
            productSearchRepository.save(product);
            log.info("Product images URL updated for productId: {}", event.getProductId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    /**
     *  Su kien update category
     * @param event
     */
    @EventHandler
    public void on(CategoryUpdateEvent event) {
        log.info("Handling CategoryUpdateEvent for categoryId: {}", event.getId());

        // Lấy danh sách các sản phẩm có category.id = event.getCategoryId()
        var products = productSearchRepository.findByCategoryId(event.getId());

        for (ProductDocument productDoc : products) {
            // Cập nhật thông tin category
            ProductDocument.CategoryDocument updatedCategory = ProductDocument.CategoryDocument.builder()
                    .id(event.getId())
                    .name(event.getName())
                    .codeName(event.getCodeName())
                    .images(event.getImages())
                    .description(event.getDescription())
                    .isActive(event.getIsActive())
                    .build();
            productDoc.setCategory(updatedCategory);
            productDoc.updateTimestamps();
        }

        // Lưu lại tất cả sản phẩm đã cập nhật
        productSearchRepository.saveAll(products);
        log.info("Updated {} products with new category information", products.size());
    }

    @EventHandler
    public void on(CategoryChangeStatusEvent event) {
        log.info("Handling CategoryChangeStatusEvent for categoryId: {}", event.getId());

        // Lấy danh sách các sản phẩm có category.id = event.getCategoryId()
        var products = productSearchRepository.findByCategoryId(event.getId());

        for (ProductDocument productDoc : products) {
            // Cập nhật thông tin category
            ProductDocument.CategoryDocument updatedCategory = ProductDocument.CategoryDocument.builder()
                    .id(event.getId())
                    .isActive(event.getIsActive())
                    .build();
            productDoc.setCategory(updatedCategory);
            productDoc.updateTimestamps();
        }

        // Lưu lại tất cả sản phẩm đã cập nhật
        productSearchRepository.saveAll(products);
        log.info("Updated {} products with new category information", products.size());
    }

    /**
     *  Su kien update color
     * @param event
     */
    @EventHandler
    public void on(ColorUpdateEvent event) {
        log.info("Handling ColorUpdateEvent for colorId: {}", event.getId());

        // Tìm tất cả các ProductDocument có productColors chứa Color có id = event.getId()
        List<ProductDocument> products = productSearchRepository.findByProductColorsColorId(event.getId());

        // Với mỗi ProductDocument, duyệt qua danh sách productColors và cập nhật thông tin của Color nếu cần
        for (ProductDocument productDoc : products) {
            List<ProductDocument.ProductColorDocument> updatedColors = productDoc.getProductColors().stream()
                    .map(pc -> {
                        if (pc.getColor() != null && event.getId().equals(pc.getColor().getId())) {
                            // Tạo đối tượng Color mới với thông tin cập nhật
                            ProductDocument.ProductColorDocument.ColorDocument updatedColor =
                                    ProductDocument.ProductColorDocument.ColorDocument.builder()
                                            .id(event.getId())
                                            .name(event.getName())
                                            .codeName(event.getCodeName())
                                            .colorCode(event.getColorCode())
                                            .description(event.getDescription())
                                            .isActive(event.getIsActive())
                                            .build();
                            pc.setColor(updatedColor);
                        }
                        return pc;
                    })
                    .collect(Collectors.toList());
            productDoc.setProductColors(updatedColors);
            productDoc.updateTimestamps();
        }

        // Lưu tất cả các ProductDocument đã cập nhật
        productSearchRepository.saveAll(products);
        log.info("Updated {} products with new color information", products.size());
    }

    @EventHandler
    public void on(ColorChangeStatusEvent event) {
        log.info("Handling ColorChangeStatusEvent for colorId: {}", event.getId());

        // Tìm tất cả các ProductDocument có productColors chứa Color có id = event.getId()
        List<ProductDocument> products = productSearchRepository.findByProductColorsColorId(event.getId());

        // Với mỗi ProductDocument, duyệt qua danh sách productColors và cập nhật thông tin của Color nếu cần
        for (ProductDocument productDoc : products) {
            List<ProductDocument.ProductColorDocument> updatedColors = productDoc.getProductColors().stream()
                    .map(pc -> {
                        if (pc.getColor() != null && event.getId().equals(pc.getColor().getId())) {
                            // Tạo đối tượng Color mới với thông tin cập nhật
                            ProductDocument.ProductColorDocument.ColorDocument updatedColor =
                                    ProductDocument.ProductColorDocument.ColorDocument.builder()
                                            .id(event.getId())
                                            .isActive(event.getIsActive())
                                            .build();
                            pc.setColor(updatedColor);
                        }
                        return pc;
                    })
                    .collect(Collectors.toList());
            productDoc.setProductColors(updatedColors);
            productDoc.updateTimestamps();
        }

        // Lưu tất cả các ProductDocument đã cập nhật
        productSearchRepository.saveAll(products);
        log.info("Updated {} products with new color information", products.size());
    }

    /**
     *  Su kien update color
     * @param event
     */
    @EventHandler
    public void on(SizeUpdateEvent event) {
        log.info("Handling SizeUpdateEvent for sizeId: {}", event.getId());

        // Tìm tất cả các ProductDocument có productColors.productVariants chứa size có id = event.getId()
        List<ProductDocument> products = productSearchRepository.findByProductColorsProductVariantsSizeId(event.getId());

        for (ProductDocument productDoc : products) {
            List<ProductDocument.ProductColorDocument> updatedColors = productDoc.getProductColors().stream()
                    .map(pc -> {
                        if (pc.getProductVariants() != null) {
                            List<ProductDocument.ProductColorDocument.ProductVariantDocument> updatedVariants =
                                    pc.getProductVariants().stream()
                                            .map(variant -> {
                                                if (variant.getSize() != null && event.getId().equals(variant.getSize().getId())) {
                                                    // Tạo đối tượng Size mới với thông tin cập nhật
                                                    ProductDocument.ProductColorDocument.ProductVariantDocument.SizeDocument updatedSize =
                                                            ProductDocument.ProductColorDocument.ProductVariantDocument.SizeDocument.builder()
                                                                    .id(event.getId())
                                                                    .name(event.getName())
                                                                    .codeName(event.getCodeName())
                                                                    .isActive(event.getIsActive())
                                                                    .build();
                                                    variant.setSize(updatedSize);
                                                }
                                                return variant;
                                            })
                                            .collect(Collectors.toList());
                            pc.setProductVariants(updatedVariants);
                        }
                        return pc;
                    })
                    .collect(Collectors.toList());
            productDoc.setProductColors(updatedColors);
            productDoc.updateTimestamps();
        }

        productSearchRepository.saveAll(products);
        log.info("Updated {} products with new size information", products.size());
    }

    @EventHandler
    public void on(SizeChangeStatusEvent event) {
        log.info("Handling SizeChangeStatusEvent for sizeId: {}", event.getId());

        // Tìm tất cả các ProductDocument có productColors.productVariants chứa size có id = event.getId()
        List<ProductDocument> products = productSearchRepository.findByProductColorsProductVariantsSizeId(event.getId());

        for (ProductDocument productDoc : products) {
            List<ProductDocument.ProductColorDocument> updatedColors = productDoc.getProductColors().stream()
                    .map(pc -> {
                        if (pc.getProductVariants() != null) {
                            List<ProductDocument.ProductColorDocument.ProductVariantDocument> updatedVariants =
                                    pc.getProductVariants().stream()
                                            .map(variant -> {
                                                if (variant.getSize() != null && event.getId().equals(variant.getSize().getId())) {
                                                    // Tạo đối tượng Size mới với thông tin cập nhật
                                                    ProductDocument.ProductColorDocument.ProductVariantDocument.SizeDocument updatedSize =
                                                            ProductDocument.ProductColorDocument.ProductVariantDocument.SizeDocument.builder()
                                                                    .id(event.getId())
                                                                    .isActive(event.getIsActive())
                                                                    .build();
                                                    variant.setSize(updatedSize);
                                                }
                                                return variant;
                                            })
                                            .collect(Collectors.toList());
                            pc.setProductVariants(updatedVariants);
                        }
                        return pc;
                    })
                    .collect(Collectors.toList());
            productDoc.setProductColors(updatedColors);
            productDoc.updateTimestamps();
        }

        productSearchRepository.saveAll(products);
        log.info("Updated {} products with new size information", products.size());
    }

    /**
     *  Su kien update promotion
     * @param event
     */
    @EventHandler
    public void on(PromotionUpdateEvent event) {
        log.info("Handling PromotionUpdateEvent for promotionId: {}", event.getId());

        // Tìm tất cả ProductDocument có chứa promotion cần cập nhật
        List<ProductDocument> products = productSearchRepository.findByProductColorsPromotionsId(event.getId());

        for (ProductDocument productDoc : products) {
            List<ProductDocument.ProductColorDocument> updatedColors = productDoc.getProductColors().stream()
                    .map(pc -> {
                        // Duyệt qua từng promotion trong danh sách và cập nhật
                        List<ProductDocument.ProductColorDocument.PromotionDocument> updatedPromotions =
                                pc.getPromotions().stream()
                                        .map(promo -> {
                                            if (event.getId().equals(promo.getId())) {
                                                // Cập nhật thông tin promotion
                                                return ProductDocument.ProductColorDocument.PromotionDocument.builder()
                                                        .id(event.getId())
                                                        .name(event.getName())
                                                        .codeName(event.getCodeName())
                                                        .discountPercentage(event.getDiscountPercentage())
                                                        .startDate(event.getStartDate())
                                                        .endDate(event.getEndDate())
                                                        .isActive(event.getIsActive())
                                                        .build();
                                            }
                                            return promo; // Giữ nguyên nếu không trùng ID
                                        })
                                        .collect(Collectors.toList());

                        pc.setPromotions(updatedPromotions);
                        return pc;
                    })
                    .collect(Collectors.toList());

            productDoc.setProductColors(updatedColors);
            productDoc.updateTimestamps();
        }

        productSearchRepository.saveAll(products);
        log.info("Updated {} products with promotion changes", products.size());
    }

    @EventHandler
    public void on(PromotionChangeStatusEvent event) {
        log.info("Handling PromotionChangeStatusEvent for promotionId: {}", event.getId());

        List<ProductDocument> products = productSearchRepository.findByProductColorsPromotionsId(event.getId());

        for (ProductDocument productDoc : products) {
            List<ProductDocument.ProductColorDocument> updatedColors = productDoc.getProductColors().stream()
                    .map(pc -> {
                        List<ProductDocument.ProductColorDocument.PromotionDocument> updatedPromotions =
                                pc.getPromotions().stream()
                                        .map(promo -> {
                                            if (event.getId().equals(promo.getId())) {
                                                // Chỉ cập nhật trạng thái isActive
                                                return promo.builder()
                                                        .isActive(event.getIsActive())
                                                        .build();
                                            }
                                            return promo;
                                        })
                                        .collect(Collectors.toList());

                        // Cập nhật danh sách và tính lại finalPrice
                        pc.setPromotions(updatedPromotions);
                        return pc;
                    })
                    .collect(Collectors.toList());

            productDoc.setProductColors(updatedColors);
            productDoc.updateTimestamps();
        }

        productSearchRepository.saveAll(products);
        log.info("Updated {} products with promotion status changes", products.size());
    }
}

