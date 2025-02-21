package com.luanvan.commonservice.utils;

import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchParamsUtils {
    public static Sort getSortParams(ArrayList<String> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            sorts = new ArrayList<>(List.of("createdAt-DESC"));
        }

        return Sort.by(
                sorts.stream()
                        .map(order -> {
                            String[] parts = order.split("-");
                            String field = parts[0].trim();
                            Sort.Direction direction = (parts.length > 1)
                                    ? Sort.Direction.fromString(parts[1].trim().toUpperCase())
                                    : Sort.Direction.ASC;
                            return new Sort.Order(direction, field);
                        })
                        .collect(Collectors.toList())
        );
    }
}
