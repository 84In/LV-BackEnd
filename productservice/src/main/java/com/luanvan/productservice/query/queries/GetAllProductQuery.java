package com.luanvan.productservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllProductQuery {
    private String query;
    private String category;
    private ArrayList<String> price;
    private ArrayList<String> size;
    private ArrayList<String> color;
    private int pageNumber;
    private int pageSize;
    private ArrayList<String> sortOrder;
}
