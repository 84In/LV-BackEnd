package com.luanvan.productservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.productservice.command.event.ProductRollBackStockEvent;
import com.luanvan.productservice.command.event.ProductChangeStatusEvent;
import com.luanvan.productservice.command.event.ProductCreateEvent;
import com.luanvan.productservice.command.event.ProductUpdateEvent;
import com.luanvan.productservice.command.event.ProductUpdateStockEvent;
import com.luanvan.productservice.entity.*;
import com.luanvan.productservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    @CacheEvict(value = "products", allEntries = true)
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
                    .isActive(colorItem.getIsActive())
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
                                .isActive(variantItem.getIsActive())
                                .productColor(productColor) // Gán ProductColor đã lưu
                                .build();
                    }).collect(Collectors.toList());

            productVariantRepository.saveAll(productVariants);
        }
    }

    @EventHandler
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void on(ProductUpdateEvent event) {
        log.info("Product updated");

        // 1. Cập nhật thông tin cơ bản của Product
        Product product = productRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        Category category = categoryRepository.findById(event.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        product.setName(event.getName());
        product.setDescription(event.getDescription());
        product.setImages(event.getImages());
        product.setCategory(category);
        var finalProduct = productRepository.save(product);

        // 2. Lấy toàn bộ ProductColor hiện có của Product
        List<ProductColor> existingColors = productColorRepository.findAllByProduct(finalProduct);
        var existingColorMap = existingColors.stream()
                .collect(Collectors.toMap(ProductColor::getId, pc -> pc));

        // Danh sách kết quả cập nhật cuối cùng
        List<ProductColor> finalProductColors = new ArrayList<>();

        // 3. Duyệt qua danh sách ProductColor từ event
        for (ProductUpdateEvent.ProductColorUpdateEvent colorEvent : event.getProductColors()) {
            ProductColor productColor;
            if (existingColorMap.containsKey(colorEvent.getId())) {
                // Nếu đã tồn tại thì cập nhật và loại bỏ khỏi map
                productColor = existingColorMap.get(colorEvent.getId());
                existingColorMap.remove(colorEvent.getId());
            } else {
                // Nếu chưa có thì tạo mới và gán Product cho nó
                productColor = new ProductColor();
                productColor.setId(colorEvent.getId());
                productColor.setProduct(finalProduct);
            }

            // Cập nhật thông tin ProductColor
            Color color = colorRepository.findById(colorEvent.getColorId())
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));
            productColor.setColor(color);
            productColor.setPrice(colorEvent.getPrice());
            productColor.setIsActive(colorEvent.getIsActive());

            // Cập nhật danh sách promotions
            List<Promotion> promotions = colorEvent.getPromotions().stream()
                    .map(promoId -> promotionRepository.findById(promoId)
                            .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED)))
                    .collect(Collectors.toList());
            productColor.setPromotions(promotions);

            // 4. Xử lý các ProductVariant cho ProductColor
            List<ProductVariant> existingVariants = productVariantRepository.findAllByProductColor(productColor);
            var existingVariantMap = existingVariants.stream()
                    .collect(Collectors.toMap(ProductVariant::getId, v -> v));

            List<ProductVariant> updatedVariants = new ArrayList<>();

            // Duyệt qua danh sách variant từ event
            for (ProductUpdateEvent.ProductVariantUpdateEvent variantEvent : colorEvent.getProductVariants()) {
                ProductVariant productVariant;
                if (existingVariantMap.containsKey(variantEvent.getId())) {
                    productVariant = existingVariantMap.get(variantEvent.getId());
                    existingVariantMap.remove(variantEvent.getId());
                } else {
                    productVariant = new ProductVariant();
                    productVariant.setId(variantEvent.getId());
                    productVariant.setProductColor(productColor);
                }
                // Cập nhật thông tin ProductVariant
                Size size = sizeRepository.findById(variantEvent.getSizeId())
                        .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));
                productVariant.setSize(size);
                productVariant.setStock(variantEvent.getStock());
                productVariant.setIsActive(variantEvent.getIsActive());

                updatedVariants.add(productVariant);
            }
            // Với các ProductVariant không được truyền xuống event, chuyển trạng thái isActive sang false
            for (ProductVariant remainingVariant : existingVariantMap.values()) {
                remainingVariant.setIsActive(false);
                updatedVariants.add(remainingVariant);
            }
            // Gán lại danh sách variant cho ProductColor
            productColor.setProductVariants(updatedVariants);
            finalProductColors.add(productColor);
        }

        // Với các ProductColor hiện có nhưng không được cập nhật trong event, chuyển trạng thái isActive sang false
        for (ProductColor remainingColor : existingColorMap.values()) {
            remainingColor.setIsActive(false);
            List<ProductVariant> variants = productVariantRepository.findAllByProductColor(remainingColor);
            for (ProductVariant variant : variants) {
                variant.setIsActive(false);
            }
            finalProductColors.add(remainingColor);
        }

        // 5. Lưu lại danh sách ProductColor đã cập nhật
        productColorRepository.saveAll(finalProductColors);
    }

    @EventHandler
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void on(ProductChangeStatusEvent event) {
        var product = productRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        product.setIsActive(event.getIsActive());
        productRepository.save(product);
    }

    @EventHandler
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void on(ProductUpdateStockEvent event) {
        log.info("ProductUpdateStockEvent for productId: {}", event.getId());
        var product = productRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        var productVariant = product.getProductColors().stream()
                .filter(pc -> pc.getColor().getId().equals(event.getColorId()))
                .flatMap(pc -> pc.getProductVariants().stream())
                .filter(pv -> pv.getSize().getId().equals(event.getSizeId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_EXISTED));
        if(event.getQuantity() > productVariant.getStock()){
            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
        // Cập nhật stock và sold
        int quantity = event.getQuantity();
        int updatedStock = productVariant.getStock() - quantity;
        int updatedSold = productVariant.getSold() + quantity;

        productVariant.setStock(updatedStock);
        productVariant.setSold(updatedSold);

        productRepository.save(product);
    }

    @EventHandler
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void on(ProductRollBackStockEvent event) {
        log.info("ProductRollBackStockEvent for productId: {}", event.getId());
        var product = productRepository.findById(event.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        var productVariant = product.getProductColors().stream()
                .filter(pc -> pc.getColor().getId().equals(event.getColorId()))
                .flatMap(pc -> pc.getProductVariants().stream())
                .filter(pv -> pv.getSize().getId().equals(event.getSizeId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_EXISTED));

        // Cập nhật stock và sold
        int quantity = event.getQuantity();
        int updatedStock = productVariant.getStock() + quantity;
        int updatedSold = productVariant.getSold() - quantity;

        productVariant.setStock(updatedStock);
        productVariant.setSold(updatedSold);

        productRepository.save(product);
    }

}
