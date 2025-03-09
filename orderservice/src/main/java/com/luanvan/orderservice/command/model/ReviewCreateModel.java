package com.luanvan.orderservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateModel {
    private Integer rating;
    private String comment;
    private String userId;
    private String orderDetailId;
    private String productId;
}
