package com.luanvan.productservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllPromotionQuery {
    private int page;
    private int size;
    private ArrayList<String> sortOrder;
}
