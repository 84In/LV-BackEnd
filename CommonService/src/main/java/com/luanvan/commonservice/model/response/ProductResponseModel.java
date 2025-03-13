package com.luanvan.commonservice.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseModel implements Serializable {
    private String id;
    private String name;
    private String description;
    private String images;
    private Boolean isActive;
    private Category category;
    private Collection<ProductColor> productColors;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Category implements Serializable  {
        private String id;
        private String name;
        private String codeName;
        private String images;
        private String description;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductColor implements Serializable  {
        private String id;
        private BigDecimal price;
        private BigDecimal finalPrice;
        private Boolean isActive;
        private Color color;
        private Promotion promotion;
        private Collection<ProductVariant> productVariants;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Color implements Serializable  {
        private String id;
        private String name;
        private String codeName;
        private String colorCode;
        private String description;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Promotion implements Serializable  {
        private String id;
        private String name;
        private String codeName;
        private Double discountPercentage;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductVariant implements Serializable  {
        private String id;
        private Size size;
        private Integer stock;
        private Integer sold;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Size implements Serializable  {
        private String id;
        private String name;
        private String codeName;
        private Boolean isActive;
    }

}
