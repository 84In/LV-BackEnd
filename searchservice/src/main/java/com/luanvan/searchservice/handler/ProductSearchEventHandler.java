package com.luanvan.searchservice.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.event.*;
import com.luanvan.searchservice.entity.ProductDocument;
import com.luanvan.searchservice.mapper.ProductDocumentMapper;
import com.luanvan.searchservice.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchEventHandler {
    private final ProductSearchRepository productSearchRepository;
    private final QueryGateway queryGateway;
    private final ProductDocumentMapper productDocumentMapper;

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

        ProductDocument updatedProduct = productDocumentMapper.buildProductDocument(event);
        updatedProduct.updateTimestamps();
        productSearchRepository.save(updatedProduct);
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

        // Tìm tất cả các ProductDocument có productColors có promotion với id = event.getPromotionId()
        List<ProductDocument> products = productSearchRepository.findByProductColorsPromotionId(event.getId());

        for (ProductDocument productDoc : products) {
            List<ProductDocument.ProductColorDocument> updatedColors = productDoc.getProductColors().stream()
                    .map(pc -> {
                        if (pc.getPromotion() != null && event.getId().equals(pc.getPromotion().getId())) {
                            // Tạo đối tượng Promotion mới với thông tin cập nhật
                            ProductDocument.ProductColorDocument.PromotionDocument updatedPromotion =
                                    ProductDocument.ProductColorDocument.PromotionDocument.builder()
                                            .id(event.getId())
                                            .name(event.getName())
                                            .codeName(event.getCodeName())
                                            .discountPercentage(event.getDiscountPercentage())
                                            .startDate(event.getStartDate())
                                            .endDate(event.getEndDate())
                                            .isActive(event.getIsActive())
                                            .build();
                            pc.setPromotion(updatedPromotion);
                        }
                        return pc;
                    })
                    .collect(Collectors.toList());
            productDoc.setProductColors(updatedColors);
            productDoc.updateTimestamps();
        }

        productSearchRepository.saveAll(products);
        log.info("Updated {} products with new promotion information", products.size());
    }

    @EventHandler
    public void on(PromotionChangeStatusEvent event) {
        log.info("Handling PromotionChangeStatusEvent for promotionId: {}", event.getId());

        // Tìm tất cả các ProductDocument có productColors có promotion với id = event.getPromotionId()
        List<ProductDocument> products = productSearchRepository.findByProductColorsPromotionId(event.getId());

        for (ProductDocument productDoc : products) {
            List<ProductDocument.ProductColorDocument> updatedColors = productDoc.getProductColors().stream()
                    .map(pc -> {
                        if (pc.getPromotion() != null && event.getId().equals(pc.getPromotion().getId())) {
                            // Tạo đối tượng Promotion mới với thông tin cập nhật
                            ProductDocument.ProductColorDocument.PromotionDocument updatedPromotion =
                                    ProductDocument.ProductColorDocument.PromotionDocument.builder()
                                            .id(event.getId())
                                            .isActive(event.getIsActive())
                                            .build();
                            pc.setPromotion(updatedPromotion);
                        }
                        return pc;
                    })
                    .collect(Collectors.toList());
            productDoc.setProductColors(updatedColors);
            productDoc.updateTimestamps();
        }

        productSearchRepository.saveAll(products);
        log.info("Updated {} products with new promotion information", products.size());
    }
}

