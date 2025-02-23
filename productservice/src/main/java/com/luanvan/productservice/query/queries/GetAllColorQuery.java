package com.luanvan.productservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllColorQuery {
    private int pageNumber;
    private int pageSize;
    private ArrayList<String> sortOrder;
}
