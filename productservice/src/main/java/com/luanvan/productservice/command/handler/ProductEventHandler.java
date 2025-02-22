package com.luanvan.productservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.productservice.command.event.ProductCreateEvent;
import com.luanvan.productservice.entity.*;
import com.luanvan.productservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventHandler {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SizeRepository sizeRepository;
    private final PromotionRepository promotionRepository;
    private final ColorRepository colorRepository;
    private final ProductColorRepository productColorRepository;
    private final ProductVariantRepository productVariantRepository;

    @EventHandler
    @Transactional
    public void on(ProductCreateEvent event) {
        log.info("Product created");

        // 1. Lưu Product
        Category category = categoryRepository.findById(event.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        Product product = Product.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .images(event.getImages())
                .category(category)
                .build();
        Product finalProduct = productRepository.save(product);

        // 2. Lưu ProductColor chưa có promotions
        List<ProductColor> productColors = event.getProductColors().stream().map(colorItem -> {
            Color color = colorRepository.findById(colorItem.getColorId())
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));

            return ProductColor.builder()
                    .id(colorItem.getId())
                    .price(colorItem.getPrice())
                    .color(color)
                    .product(finalProduct) // Gán product đã lưu
                    .build();
        }).collect(Collectors.toList());
        var finalProductColors = productColorRepository.saveAll(productColors);

        // 3. Cập nhật promotions cho ProductColor đã lưu
        for (ProductColor productColor : finalProductColors) {
            List<Promotion> promotions = event.getProductColors().stream()
                    .filter(pc -> pc.getId().equals(productColor.getId()))
                    .flatMap(pc -> pc.getPromotions().stream())
                    .map(promotionId -> promotionRepository.findById(promotionId)
                            .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED)))
                    .collect(Collectors.toList());

            productColor.setPromotions(promotions);
        }
        productColorRepository.saveAll(finalProductColors);

        // 4. Lưu ProductVariant
        for (ProductColor productColor : finalProductColors) {
            List<ProductVariant> productVariants = event.getProductColors().stream()
                    .filter(pc -> pc.getId().equals(productColor.getId()))
                    .flatMap(pc -> pc.getProductVariants().stream())
                    .map(variantItem -> {
                        Size size = sizeRepository.findById(variantItem.getSizeId())
                                .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));

                        return ProductVariant.builder()
                                .id(variantItem.getId())
                                .size(size)
                                .stock(variantItem.getStock())
                                .productColor(productColor) // Gán ProductColor đã lưu
                                .build();
                    }).collect(Collectors.toList());

            productVariantRepository.saveAll(productVariants);
        }
    }
}
