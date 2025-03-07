package com.luanvan.orderservice.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageOrderResponse {
    private ArrayList<OrderResponseModel> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}

