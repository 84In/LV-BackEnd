package com.luanvan.commonservice.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductWithFilterQuery {
    private String query;
    private String category;
    private String price;
    private String size;
    private String color;
    private int pageNumber = 0;
    private int pageSize = 10;
    private String sortOrder;
}
