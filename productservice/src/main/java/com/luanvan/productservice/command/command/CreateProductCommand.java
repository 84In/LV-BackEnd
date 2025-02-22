package com.luanvan.productservice.command.command;

import com.luanvan.productservice.command.model.ProductCreateModel;
import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductCommand {
    @TargetAggregateIdentifier
    private String id;
    private String name;
    private String description;
    private String images;
    private String categoryId;
    private Boolean isActive;
    private List<CreateProductColorCommand> productColors;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProductColorCommand {
        private String id;
        private String colorId;
        private BigDecimal price;
        private Boolean isActive;
        private List<CreateProductVariantCommand> productVariants;
        private List<String> promotions;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateProductVariantCommand {
        private String id;
        private String sizeId;
        private Integer stock;
    }
}
