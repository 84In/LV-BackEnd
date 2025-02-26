package com.luanvan.commonservice.utils;

import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lấy giá trị sort từ params để pagination
 * @param sorts Tập hợp các giá trị sort có dạng: sortKey-sortDirection, ...
 * @return Danh sách Sort.Order các giá trị sort đã tách ra.
 */
public class SearchParamsUtils {
    // Mapping sort key (ngắn) sang property path trong entity Product (hoặc các join liên quan)
    private static final Map<String, String> SORT_MAPPING = new HashMap<>();
    static {
        // Có thể thêm nhiều sự lựa sort theo các giá trị đặc biệt
        SORT_MAPPING.put("sold", "productColors.productVariants.sold");
        SORT_MAPPING.put("price", "productColors.price");
    }

    public static Sort getSortParams(String sorts) {
        // Giá trị mặc định
        if (sorts == null || sorts.isBlank()) {
            sorts = "createdAt-DESC";
        }
        List<String> sortOrder = List.of(sorts.split(",")).stream()
                .map(String::trim)
                .filter(order -> !order.isEmpty())
                .collect(Collectors.toList());

        return Sort.by(
                sortOrder.stream().map(order -> {
                    String[] parts = order.split("-");
                    String key = parts[0].trim();
                    if (key.isEmpty()) {
                        throw new IllegalArgumentException("Sort property must not be null or empty");
                    }
                    // Nếu key có trong mapping, dùng property path tương ứng, nếu không dùng key ban đầu
                    String propertyPath = SORT_MAPPING.getOrDefault(key, key);
                    Sort.Direction direction = (parts.length > 1)
                            ? Sort.Direction.fromString(parts[1].trim().toUpperCase())
                            : Sort.Direction.ASC;
                    return new Sort.Order(direction, propertyPath);
                }).collect(Collectors.toList())
        );
    }
}
