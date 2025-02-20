package com.luanvan.productservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllSizeQuery {
    private int page;
    private int size;
    private String sortBy;
    private Sort.Direction sortDirection;
}
