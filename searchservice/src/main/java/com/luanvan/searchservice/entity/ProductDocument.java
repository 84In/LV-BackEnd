package com.luanvan.searchservice.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "products_index")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Text)
    private String images;

    @Field(type = FieldType.Boolean)
    private Boolean isActive;

    @Field(type = FieldType.Object)
    private CategoryDocument category;

    @Field(type = FieldType.Nested)
    private List<ProductColorDocument> productColors = new ArrayList<>();

    @CreatedDate
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedAt;

    public void updateTimestamps() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryDocument implements Serializable {
        private String id;

        @Field(type = FieldType.Text, analyzer = "standard")
        private String name;

        @Field(type = FieldType.Keyword)
        private String codeName;

        @Field(type = FieldType.Text)
        private String  images;

        @Field(type = FieldType.Text)
        private String description;

        @Field(type = FieldType.Boolean)
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductColorDocument implements Serializable {
        private String id;

        @Field(type = FieldType.Double)
        private BigDecimal price;

        @Field(type = FieldType.Boolean)
        private Boolean isActive;

        @Field(type = FieldType.Object)
        private ColorDocument color;

        @Field(type = FieldType.Nested)
        private List<PromotionDocument> promotions = new ArrayList<>();

        @Field(type = FieldType.Nested)
        private List<ProductVariantDocument> productVariants = new ArrayList<>();

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ColorDocument implements Serializable {
            private String id;

            @Field(type = FieldType.Text)
            private String name;

            @Field(type = FieldType.Keyword)
            private String codeName;

            @Field(type = FieldType.Keyword)
            private String colorCode;

            @Field(type = FieldType.Text)
            private String description;

            @Field(type = FieldType.Boolean)
            private Boolean isActive;
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class PromotionDocument implements Serializable {
            private String id;

            @Field(type = FieldType.Text)
            private String name;

            @Field(type = FieldType.Keyword)
            private String codeName;

            @Field(type = FieldType.Double)
            private Double discountPercentage;

            @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
            private LocalDateTime startDate;

            @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
            private LocalDateTime endDate;

            @Field(type = FieldType.Boolean)
            private Boolean isActive;
        }

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ProductVariantDocument implements Serializable {
            private String id;

            @Field(type = FieldType.Object)
            private SizeDocument size;

            @Field(type = FieldType.Integer)
            private Long stock;

            @Builder.Default
            @Field(type = FieldType.Integer)
            private Long sold = 0L;

            @Field(type = FieldType.Boolean)
            private Boolean isActive;

            @Getter
            @Setter
            @Builder
            @AllArgsConstructor
            @NoArgsConstructor
            public static class SizeDocument implements Serializable {
                private String id;

                @Field(type = FieldType.Text)
                private String name;

                @Field(type = FieldType.Keyword)
                private String codeName;

                @Field(type = FieldType.Boolean)
                private Boolean isActive;
            }
        }
    }
}
