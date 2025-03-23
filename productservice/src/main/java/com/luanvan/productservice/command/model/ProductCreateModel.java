package com.luanvan.productservice.command.model;

import com.luanvan.productservice.entity.ProductVariant;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateModel {
    private String name;
    private String description;
    private String images;
    private String categoryId;
    private List<ProductColorModel> productColors = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductColorModel {
        private BigDecimal price;
        private String colorId;
        private List<ProductVariantModel> productVariants = new ArrayList<>();
        private List<String> promotions = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductVariantModel {
        private String sizeId;
        private Long stock;
    }
}
