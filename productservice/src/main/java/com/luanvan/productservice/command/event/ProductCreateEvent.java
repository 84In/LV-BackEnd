package com.luanvan.productservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateEvent {
    private String id;
    private String name;
    private String description;
    private String images;
    private String categoryId;
    private Boolean isActive;
    private List<ProductColorCreateEvent> productColors;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductColorCreateEvent {
        private String id;
        private String colorId;
        private BigDecimal price;
        private Boolean isActive;
        private List<ProductVariantCreateEvent> productVariants;
        private List<String> promotions;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductVariantCreateEvent {
        private String id;
        private String sizeId;
        private Integer stock;
        private Boolean isActive;
    }
}

