package com.luanvan.productservice.entity;


/*
 * Dùng để search sản phẩm nhanh chóng hiển thị thông tin cần thiết
 * */

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.util.Collection;

@Document(indexName = "product_variant_index")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {
    @Id
    private String id;
    private String name;
    private String description;
    private String category;
    private Collection<Color> colors;
    private Collection<Size> sizes;
    private BigDecimal price;
    private Integer stock;
    private Collection<Promotion> promotions;
}
