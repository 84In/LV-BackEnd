package com.luanvan.commonservice.utils;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lấy giá trị sort từ params để pagination
 * @param sorts Tập hợp các giá trị sort có dạng: sortBy-sortDirection, sortBy2-sortDirection2, ...
 * @return Danh sách Sort.Order các giá trị sort đã tách ra.
 */
public class SearchParamsUtils {
    public static Sort getSortParams(String sorts) {
        // Nếu chuỗi rỗng hoặc chỉ chứa khoảng trắng, sử dụng giá trị mặc định
        if (sorts == null || sorts.isBlank()) {
            sorts = "createdAt-DESC";
        }

        List<String> sortOrder = List.of(sorts.split(","));

        // Loại bỏ các phần tử rỗng (nếu có)
        sortOrder = sortOrder.stream()
                .filter(order -> !order.trim().isEmpty())
                .collect(Collectors.toList());

        return Sort.by(
                sortOrder.stream().map(order -> {
                    String[] parts = order.split("-");
                    String field = parts[0].trim();
                    if (field.isEmpty()) {
                        throw new IllegalArgumentException("Sort property must not be null or empty");
                    }
                    Sort.Direction direction = (parts.length > 1)
                            ? Sort.Direction.fromString(parts[1].trim().toUpperCase())
                            : Sort.Direction.ASC;
                    return new Sort.Order(direction, field);
                }).collect(Collectors.toList())
        );
    }
}
