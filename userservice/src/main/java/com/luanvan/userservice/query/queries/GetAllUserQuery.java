package com.luanvan.userservice.query.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUserQuery {
    private int page;
    private int size;
    private String sortBy;
    private Sort.Direction sortDirection;
}
