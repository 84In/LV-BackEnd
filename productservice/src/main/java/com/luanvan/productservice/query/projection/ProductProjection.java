package com.luanvan.productservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.PageAllProductResponse;
import com.luanvan.commonservice.model.response.PageProductResponse;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.commonservice.utils.PromotionUtils;
import com.luanvan.productservice.entity.*;
import com.luanvan.commonservice.model.response.AllProductResponseModel;
import com.luanvan.productservice.query.queries.GetAllProductQuery;
import com.luanvan.productservice.query.queries.GetAllProductWithFilterQuery;
import com.luanvan.productservice.repository.ProductRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductProjection {
    private final ProductRepository productRepository;

    @QueryHandler
    public PageAllProductResponse handle(GetAllProductQuery queryParams) {
        log.info("Get all products for admin");

        // Xử lý các tham số filter: price, color, size
        List<String> priceList = (queryParams.getPrice() != null && !queryParams.getPrice().isBlank())
                ? List.of(queryParams.getPrice().split(","))
                : new ArrayList<>();

        List<String> colorList = (queryParams.getColor() != null && !queryParams.getColor().isBlank())
                ? List.of(queryParams.getColor().split(","))
                : new ArrayList<>();

        List<String> sizeList = (queryParams.getSize() != null && !queryParams.getSize().isBlank())
                ? List.of(queryParams.getSize().split(","))
                : new ArrayList<>();

        // Xây dựng Specification cho Product
        Specification<Product> spec = (root, cq, cb) -> {
            //cq.distinct(true);
            cq.groupBy(root.get("id"));

            List<Predicate> predicates = new ArrayList<>();
            // Join đén category
            Join<Product, Category> categoryJoin = root.join("category", JoinType.LEFT);
            // Join đến productColors -> productVariants -> size
            Join<Product, ProductColor> productColorJoin = root.join("productColors", JoinType.LEFT);
            Join<ProductColor, ProductVariant> productVariantJoin = productColorJoin.join("productVariants", JoinType.LEFT);
            Join<ProductVariant, Size> sizeJoin = productVariantJoin.join("size", JoinType.LEFT);
            // Join đến productColors -> color
            Join<ProductColor, Color> colorJoin = productColorJoin.join("color", JoinType.LEFT);

            // 1. Lọc theo query hoặc category
            if (StringUtils.hasText(queryParams.getQuery())) {
                Predicate productNameLike = cb.like(cb.lower(root.get("name")), "%" + queryParams.getQuery().toLowerCase() + "%");
                Predicate categoryNameLike = cb.like(cb.lower(categoryJoin.get("name")), "%" + queryParams.getQuery().toLowerCase() + "%");
                predicates.add(cb.or(productNameLike, categoryNameLike));
            } else if (StringUtils.hasText(queryParams.getCategory())) {
                if (!"all".equalsIgnoreCase(queryParams.getCategory())) {
                    predicates.add(cb.equal(cb.lower(categoryJoin.get("codeName")), queryParams.getCategory().toLowerCase()));
                }
            }

            // 3. Lọc theo price (min-max)
            if (!priceList.isEmpty()) {
                double globalMin = Double.MAX_VALUE;
                double globalMax = Double.MIN_VALUE;
                for (String range : priceList) {
                    if (range.isBlank()) continue;
                    String[] parts = range.split("-");
                    double min = Double.parseDouble(parts[0].trim());
                    double max = parts[1].trim().equalsIgnoreCase("infinity")
                            ? Double.MAX_VALUE
                            : Double.parseDouble(parts[1].trim());
                    globalMin = Math.min(globalMin, min);
                    globalMax = Math.max(globalMax, max);
                }
                predicates.add(cb.between(productColorJoin.get("price"), globalMin, globalMax));
            }

            // 4. Lọc theo Size
            if (!sizeList.isEmpty()) {
                predicates.add(sizeJoin.get("codeName").in(sizeList));
            }

            // 5. Lọc theo Color
            if (!colorList.isEmpty()) {
                predicates.add(colorJoin.get("codeName").in(colorList));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Tạo PageRequest từ các tham số
        Pageable pageable = PageRequest.of(queryParams.getPageNumber(), queryParams.getPageSize());
        var productPage = productRepository.findAll(spec, pageable);

        var responsePage = productPage.map(this::toAllProductResponseModel);

        return PageAllProductResponse.builder()
                .content(new ArrayList<>(responsePage.getContent()))
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();
    }

    @QueryHandler
    public PageProductResponse handle(GetAllProductWithFilterQuery queryParams) {
        log.info("Get all products with query, filter");

        // Xử lý các tham số filter: price, color, size
        List<String> priceList = (queryParams.getPrice() != null && !queryParams.getPrice().isBlank())
                ? List.of(queryParams.getPrice().split(","))
                : new ArrayList<>();

        List<String> colorList = (queryParams.getColor() != null && !queryParams.getColor().isBlank())
                ? List.of(queryParams.getColor().split(","))
                : new ArrayList<>();

        List<String> sizeList = (queryParams.getSize() != null && !queryParams.getSize().isBlank())
                ? List.of(queryParams.getSize().split(","))
                : new ArrayList<>();

        // Xây dựng Specification cho Product
        Specification<Product> spec = (root, cq, cb) -> {
            // Sử dụng GROUP BY theo Product.id
            cq.groupBy(root.get("id"));

            List<Predicate> predicates = new ArrayList<>();

            // Join các bảng liên quan
            Join<Product, Category> categoryJoin = root.join("category", JoinType.LEFT);
            Join<Product, ProductColor> productColorJoin = root.join("productColors", JoinType.LEFT);
            Join<ProductColor, ProductVariant> productVariantJoin = productColorJoin.join("productVariants", JoinType.LEFT);
            Join<ProductVariant, Size> sizeJoin = productVariantJoin.join("size", JoinType.LEFT);
            Join<ProductColor, Color> colorJoin = productColorJoin.join("color", JoinType.LEFT);

            // Điều kiện: sản phẩm active
            predicates.add(cb.isTrue(root.get("isActive")));

            // 1. Lọc theo query hoặc category
            if (StringUtils.hasText(queryParams.getQuery())) {
                Predicate productNameLike = cb.like(cb.lower(root.get("name")), "%" + queryParams.getQuery().toLowerCase() + "%");
                Predicate categoryNameLike = cb.like(cb.lower(categoryJoin.get("name")), "%" + queryParams.getQuery().toLowerCase() + "%");
                predicates.add(cb.or(productNameLike, categoryNameLike));
            } else if (StringUtils.hasText(queryParams.getCategory())) {
                if (!"all".equalsIgnoreCase(queryParams.getCategory())) {
                    predicates.add(cb.equal(cb.lower(categoryJoin.get("codeName")), queryParams.getCategory().toLowerCase()));
                }
            }

            // 2. Áp dụng sắp xếp theo các biểu thức aggregate
            // Tính giá thấp nhất của các productColor
            var minPriceExpr = cb.min(productColorJoin.get("price"));
            // Tính tổng sold của các productVariant
            var totalSoldExpr = cb.sum(productVariantJoin.get("sold"));
            String sortParam = queryParams.getSortOrder();
            if (StringUtils.hasText(sortParam)) {
                if ("price-ASC".equalsIgnoreCase(sortParam)) {
                    cq.orderBy(cb.asc(minPriceExpr));
                } else if ("price-DESC".equalsIgnoreCase(sortParam)) {
                    cq.orderBy(cb.desc(minPriceExpr));
                } else if ("sold-DESC".equalsIgnoreCase(sortParam)) {
                    cq.orderBy(cb.desc(totalSoldExpr));
                } else if ("createdAt-DESC".equalsIgnoreCase(sortParam)) {
                    cq.orderBy(cb.desc(root.get("createdAt")));
                }
            }

            // 3. Lọc theo price (min-max)
            if (!priceList.isEmpty()) {
                double globalMin = Double.MAX_VALUE;
                double globalMax = Double.MIN_VALUE;
                for (String range : priceList) {
                    if (range.isBlank()) continue;
                    String[] parts = range.split("-");
                    double min = Double.parseDouble(parts[0].trim());
                    double max = parts[1].trim().equalsIgnoreCase("infinity")
                            ? Double.MAX_VALUE
                            : Double.parseDouble(parts[1].trim());
                    globalMin = Math.min(globalMin, min);
                    globalMax = Math.max(globalMax, max);
                }
                predicates.add(cb.between(productColorJoin.get("price"), globalMin, globalMax));
            }

            // 4. Lọc theo Size
            if (!sizeList.isEmpty()) {
                predicates.add(sizeJoin.get("codeName").in(sizeList));
            }

            // 5. Lọc theo Color
            if (!colorList.isEmpty()) {
                predicates.add(colorJoin.get("codeName").in(colorList));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Tạo PageRequest (sắp xếp đã được xử lý trong Criteria Query)
        Pageable pageable = PageRequest.of(queryParams.getPageNumber(), queryParams.getPageSize());
        var productPage = productRepository.findAll(spec, pageable);

        var responsePage = productPage.map(this::toProductResponseModel);

        return PageProductResponse.builder()
                .content(new ArrayList<>(responsePage.getContent()))
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();
    }

    @QueryHandler
    public ProductResponseModel handle(GetProductQuery queryParams) {
        log.info("Get products detail with query, filter");

        var productDetail = productRepository.findById(queryParams.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return toProductResponseModel(productDetail);
    }

    // Chuyển đổi DTO cho admin hiển thị
    public AllProductResponseModel toAllProductResponseModel(Product product) {
        return AllProductResponseModel.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .images(product.getImages())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .category(AllProductResponseModel.Category.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .codeName(product.getCategory().getCodeName())
                        .images(product.getCategory().getImages())
                        .description(product.getCategory().getDescription())
                        .isActive(product.getCategory().getIsActive())
                        .build())
                .productColors(product.getProductColors().stream()
                        .sorted(Comparator.comparing(pc -> pc.getColor().getName()))
                        .map(productColor -> AllProductResponseModel.ProductColor.builder()
                                .id(productColor.getId())
                                .price(productColor.getPrice())
                                .isActive(productColor.getIsActive())
                                .color(AllProductResponseModel.Color.builder()
                                        .id(productColor.getColor().getId())
                                        .name(productColor.getColor().getName())
                                        .codeName(productColor.getColor().getCodeName())
                                        .colorCode(productColor.getColor().getColorCode())
                                        .description(productColor.getColor().getDescription())
                                        .isActive(productColor.getColor().getIsActive())
                                        .build())
                                .promotions(productColor.getPromotions().stream().map(promotion ->
                                        AllProductResponseModel.Promotion.builder()
                                                .id(promotion.getId())
                                                .name(promotion.getName())
                                                .codeName(promotion.getCodeName())
                                                .discountPercentage(promotion.getDiscountPercentage())
                                                .startDate(promotion.getStartDate())
                                                .endDate(promotion.getEndDate())
                                                .isActive(promotion.getIsActive())
                                                .build()).collect(Collectors.toList()))
                                .productVariants(productColor.getProductVariants().stream()
                                        .sorted(Comparator.comparing(pv -> pv.getSize().getName()))
                                        .map(productVariant ->
                                                AllProductResponseModel.ProductVariant.builder()
                                                        .id(productVariant.getId())
                                                        .stock(productVariant.getStock())
                                                        .sold(productVariant.getSold())
                                                        .isActive(productVariant.getIsActive())
                                                        .size(AllProductResponseModel.Size.builder()
                                                                .id(productVariant.getSize().getId())
                                                                .name(productVariant.getSize().getName())
                                                                .codeName(productVariant.getSize().getCodeName())
                                                                .isActive(productVariant.getSize().getIsActive())
                                                                .build())
                                                        .build()).collect(Collectors.toList()))
                                .build()).collect(Collectors.toList()))
                .build();
    }

    // Chuyển đổi sang DTO hiển thị ở trang người dùng
    public ProductResponseModel toProductResponseModel(Product product) {
        return ProductResponseModel.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .images(product.getImages())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .category(ProductResponseModel.Category.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .codeName(product.getCategory().getCodeName())
                        .images(product.getCategory().getImages())
                        .description(product.getCategory().getDescription())
                        .isActive(product.getCategory().getIsActive())
                        .build())
                .productColors(product.getProductColors().stream()
                        .filter(pc -> Boolean.TRUE.equals(pc.getIsActive()))
                        .sorted(Comparator.comparing(pc -> pc.getColor().getName()))
                        .map(productColor -> {
                            // Chuyển đổi danh sách promotion của productColor sang DTO promotion
                            var finalPromotion = productColor.getPromotions().stream()
                                    .map(promotion ->
                                            ProductResponseModel.Promotion.builder()
                                                    .id(promotion.getId())
                                                    .name(promotion.getName())
                                                    .codeName(promotion.getCodeName())
                                                    .discountPercentage(promotion.getDiscountPercentage())
                                                    .startDate(promotion.getStartDate())
                                                    .endDate(promotion.getEndDate())
                                                    .isActive(promotion.getIsActive())
                                                    .build()).collect(Collectors.toList());

                            // Lấy promotion tốt nhất
                            var bestPromotionOpt = PromotionUtils.getBestPromotion(finalPromotion);
                            BigDecimal finalPrice;
                            if (bestPromotionOpt.isPresent()) {
                                finalPrice = PromotionUtils.calculateFinalPrice(productColor.getPrice(),
                                        BigDecimal.valueOf(bestPromotionOpt.get().getDiscountPercentage()));
                            } else {
                                finalPrice = productColor.getPrice();
                            }

                            return ProductResponseModel.ProductColor.builder()
                                    .id(productColor.getId())
                                    .price(productColor.getPrice())
                                    .finalPrice(finalPrice)
                                    .isActive(productColor.getIsActive())
                                    .color(ProductResponseModel.Color.builder()
                                            .id(productColor.getColor().getId())
                                            .name(productColor.getColor().getName())
                                            .codeName(productColor.getColor().getCodeName())
                                            .colorCode(productColor.getColor().getColorCode())
                                            .description(productColor.getColor().getDescription())
                                            .isActive(productColor.getColor().getIsActive())
                                            .build())
                                    .promotion(bestPromotionOpt.orElse(null))
                                    .productVariants(productColor.getProductVariants().stream()
                                            .sorted(Comparator.comparing(pv -> pv.getSize().getName()))
                                            .map(productVariant ->
                                                    ProductResponseModel.ProductVariant.builder()
                                                            .id(productVariant.getId())
                                                            .stock(productVariant.getStock())
                                                            .sold(productVariant.getSold())
                                                            .isActive(productVariant.getIsActive())
                                                            .size(ProductResponseModel.Size.builder()
                                                                    .id(productVariant.getSize().getId())
                                                                    .name(productVariant.getSize().getName())
                                                                    .codeName(productVariant.getSize().getCodeName())
                                                                    .isActive(productVariant.getSize().getIsActive())
                                                                    .build())
                                                            .build()).collect(Collectors.toList()))
                                    .build();
                        }).collect(Collectors.toList()))
                .build();
    }
}
