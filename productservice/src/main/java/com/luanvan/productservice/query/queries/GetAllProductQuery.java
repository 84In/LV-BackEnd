package com.luanvan.productservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductQuery {
    private String query;
    private String category;
    private String price;
    private String size;
    private String color;
    private int pageNumber;
    private int pageSize;
    private String sortOrder;
}
