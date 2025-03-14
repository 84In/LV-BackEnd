package com.luanvan.productservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllColorWithPageQuery {
    private int pageNumber;
    private int pageSize;
    private String sortOrder;
}
