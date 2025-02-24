package com.luanvan.productservice.command.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.command.UploadProductImagesCommand;
import com.luanvan.commonservice.utils.ImageUtils;
import com.luanvan.productservice.command.command.CreateProductCommand;
import com.luanvan.productservice.command.command.UpdateProductCommand;
import com.luanvan.productservice.command.model.ProductCreateModel;
import com.luanvan.productservice.command.model.ProductUpdateModel;
import com.luanvan.productservice.entity.Product;
import com.luanvan.productservice.entity.ProductColor;
import com.luanvan.productservice.entity.ProductVariant;
import com.luanvan.productservice.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductCommandService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private KafkaTemplate<String, UploadProductImagesCommand> kafkaTemplate;
    @Autowired
    private ProductColorRepository productColorRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;

    public HashMap<?, ?> save(ArrayList<MultipartFile> images, ProductCreateModel model) throws AppException {
        //Kiểm tra tính đúng đắn của dữ liệu nhập
        if (images.isEmpty()) {
            throw new AppException(ErrorCode.COMMAND_ERROR);
        }
        if (productRepository.existsByName(model.getName())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }
        if (categoryRepository.findById(model.getCategoryId()).isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }
        if (model.getProductColors().isEmpty()) {
            throw new AppException(ErrorCode.COLOR_NOT_EXISTED);
        } else {
            model.getProductColors().forEach(colorItem -> {
                colorRepository.findById(colorItem.getColorId())
                        .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));

                if (colorItem.getPromotions() != null) {
                    colorItem.getPromotions().forEach(promotionItem -> {
                        promotionRepository.findById(promotionItem)
                                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED));
                    });
                }

                if (colorItem.getProductVariants().isEmpty()) {
                    throw new AppException(ErrorCode.SIZE_NOT_EXISTED);
                } else {
                    colorItem.getProductVariants().forEach(variantItem -> {
                        sizeRepository.findById(variantItem.getSizeId())
                                .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));
                    });
                }
            });
        }
        // Gửi Command lưu thông tin sản phẩm
        var productCommand = CreateProductCommand.builder()
                .id(UUID.randomUUID().toString())
                .name(model.getName())
                .description(model.getDescription())
                .images(model.getImages())
                .categoryId(model.getCategoryId())
                .isActive(true)
                .productColors(model.getProductColors().stream().map(colorItem ->
                                CreateProductCommand.CreateProductColorCommand.builder()
                                        .id(UUID.randomUUID().toString())
                                        .colorId(colorItem.getColorId())
                                        .price(colorItem.getPrice())
                                        .isActive(true)
                                        .productVariants(colorItem.getProductVariants().stream().map(variantItem ->
                                                        CreateProductCommand.CreateProductVariantCommand.builder()
                                                                .id(UUID.randomUUID().toString())
                                                                .sizeId(variantItem.getSizeId())
                                                                .stock(variantItem.getStock())
                                                                .isActive(true)
                                                                .build())
                                                .collect(Collectors.toList()))
                                        .promotions(colorItem.getPromotions() != null ? colorItem.getPromotions() : new ArrayList<>())
                                        .build())
                        .collect(Collectors.toList()))
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(productCommand));

        // Convert MultipartFile thành byte[]
        List<String> imageBytes = images.stream()
                .map(file -> {
                    try {
                        return ImageUtils.encodeImageToBase64(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read image", e);
                    }
                })
                .toList();
        ArrayList<String> imageBytesArrayList = new ArrayList<>(imageBytes);

        // Gửi command upload ảnh
        log.info("Upload product images for productId: {}", productCommand.getId());
        var imagesCommand = new UploadProductImagesCommand(productCommand.getId(), imageBytesArrayList);
        kafkaTemplate.send("upload-product-images", imagesCommand);

        return result;
    }

    public HashMap<?, ?> update(String productId, ProductUpdateModel model) throws AppException {
        // Kiểm tra tính đúng đắn của dữ liệu nhập
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        if (categoryRepository.findById(model.getCategoryId()).isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }
        if (model.getProductColors().isEmpty()) {
            throw new AppException(ErrorCode.COLOR_NOT_EXISTED);
        }
        model.getProductColors().forEach(colorItem -> {
            colorRepository.findById(colorItem.getColorId())
                    .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));
            if (colorItem.getPromotions() != null) {
                colorItem.getPromotions().forEach(promotionId -> {
                    promotionRepository.findById(promotionId)
                            .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED));
                });
            }
            if (colorItem.getProductVariants().isEmpty()) {
                throw new AppException(ErrorCode.SIZE_NOT_EXISTED);
            } else {
                colorItem.getProductVariants().forEach(variantItem -> {
                    sizeRepository.findById(variantItem.getSizeId())
                            .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));
                });
            }
        });

        // Xây dựng danh sách UpdateProductColorCommand dựa vào việc kiểm tra trong DB
        List<UpdateProductCommand.UpdateProductColorCommand> updateColorCommands =
                model.getProductColors().stream().map(colorItem -> {
                    // Tìm ProductColor hiện có theo productId và colorId
                    var existingColorOpt = productColorRepository.findByProductIdAndColorId(productId, colorItem.getColorId());
                    String productColorId = existingColorOpt.map(ProductColor::getId)
                            .orElse(UUID.randomUUID().toString());

                    // Xử lý danh sách variant cho từng ProductColor
                    List<UpdateProductCommand.UpdateProductVariantCommand> updateVariantCommands =
                            colorItem.getProductVariants().stream().map(variantItem -> {
                                String productVariantId;
                                if (existingColorOpt.isPresent()) {
                                    var existingVariantOpt = productVariantRepository.findByProductColorIdAndSizeId(existingColorOpt.get().getId(), variantItem.getSizeId());
                                    productVariantId = existingVariantOpt.map(ProductVariant::getId)
                                            .orElse(UUID.randomUUID().toString());
                                } else {
                                    productVariantId = UUID.randomUUID().toString();
                                }
                                return UpdateProductCommand.UpdateProductVariantCommand.builder()
                                        .id(productVariantId)
                                        .sizeId(variantItem.getSizeId())
                                        .stock(variantItem.getStock())
                                        .isActive(true)
                                        .build();
                            }).collect(Collectors.toList());

                    return UpdateProductCommand.UpdateProductColorCommand.builder()
                            .id(productColorId)
                            .colorId(colorItem.getColorId())
                            .price(colorItem.getPrice())
                            .isActive(true)
                            .productVariants(updateVariantCommands)
                            .promotions(colorItem.getPromotions() != null ? colorItem.getPromotions() : new ArrayList<>())
                            .build();
                }).collect(Collectors.toList());

        // Xây dựng command update với thông tin đã cập nhật
        var productCommand = UpdateProductCommand.builder()
                .id(productId)
                .name(model.getName())
                .description(model.getDescription())
                .images(model.getImages())
                .categoryId(model.getCategoryId())
                .isActive(model.getIsActive())
                .productColors(updateColorCommands)
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(productCommand));
        return result;
    }
}
