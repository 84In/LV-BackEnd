package com.luanvan.orderservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserOrderQuery {
    private String userId;
    private String status;
    private int pageNumber;
    private int pageSize;
    private String sortOrder;
}
