package com.luanvan.productservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCommand {
    @TargetAggregateIdentifier
    private String id;
    private String name;
    private String description;
    private String images;
    private String categoryId;
    private Boolean isActive;
    private List<UpdateProductColorCommand> productColors;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProductColorCommand {
        private String id;
        private String colorId;
        private BigDecimal price;
        private Boolean isActive;
        private List<UpdateProductVariantCommand> productVariants;
        private List<String> promotions;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProductVariantCommand {
        private String id;
        private String sizeId;
        private Long stock;
        private Long sold;
        private Boolean isActive;
    }
}
