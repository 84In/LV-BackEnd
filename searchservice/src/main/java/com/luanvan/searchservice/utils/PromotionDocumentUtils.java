package com.luanvan.searchservice.utils;

import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.searchservice.entity.ProductDocument;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PromotionDocumentUtils {

    /**
     * Lấy promotion tốt nhất (giảm giá cao nhất) từ danh sách promotion hợp lệ.
     *
     * @param promotions Tập hợp promotion.
     * @return Optional chứa promotion tốt nhất, hoặc rỗng nếu không có promotion hợp lệ.
     */
    public static Optional<ProductDocument.ProductColorDocument.PromotionDocument> getBestPromotion(Collection<ProductDocument.ProductColorDocument.PromotionDocument> promotions) {
        if (promotions == null || promotions.isEmpty()) {
            return Optional.empty();
        }
        LocalDateTime now = LocalDateTime.now();
        // Lọc promotion hợp lệ: isActive=true, có startDate, endDate, và hiện tại nằm trong khoảng thời gian.
        List<ProductDocument.ProductColorDocument.PromotionDocument> validPromotions = promotions.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .filter(p -> p.getStartDate() != null && p.getEndDate() != null)
                .filter(p -> !now.isBefore(p.getStartDate()) && !now.isAfter(p.getEndDate()))
                .collect(Collectors.toList());
        return validPromotions.stream()
                .max(Comparator.comparing(ProductDocument.ProductColorDocument.PromotionDocument::getDiscountPercentage));
    }

    /**
     * Tính finalPrice dựa trên giá gốc và discountPercentage.
     *
     * @param price Giá gốc.
     * @param discountPercentage Phần trăm giảm giá (ví dụ: 15 cho 15%).
     * @return finalPrice = price - (price * (discountPercentage/100))
     */
    public static BigDecimal calculateFinalPrice(BigDecimal price, BigDecimal discountPercentage) {
        if (discountPercentage == null) {
            return price;
        }
        BigDecimal discountFraction = discountPercentage.divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = price.multiply(discountFraction);
        return price.subtract(discountAmount);
    }
}
