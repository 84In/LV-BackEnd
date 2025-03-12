package com.luanvan.commonservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int size;
    private int number;
}
