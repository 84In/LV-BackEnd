package com.luanvan.productservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.utils.SearchParamsUtils;
import com.luanvan.productservice.entity.*;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.productservice.query.queries.GetAllProductQuery;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.productservice.repository.ProductRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductProjection {
    private final ProductRepository productRepository;


    @QueryHandler
    public List<ProductResponseModel> handle(GetAllProductQuery queryParams) {
        log.info("Get all products with query, filter");
        // Xây dựng Specification cho Product
        Specification<Product> spec = (root, cq, cb) -> {
            cq.distinct(true);

            List<Predicate> predicates = new ArrayList<>();
            // Join đén category
            Join<Product, Category> categoryJoin = root.join("category", JoinType.LEFT);
            // Join đến productColors -> productVariants -> size
            Join<Product, ProductColor> productColorJoin = root.join("productColors", JoinType.LEFT);
            Join<ProductColor, ProductVariant> productVariantJoin = productColorJoin.join("productVariants", JoinType.LEFT);
            Join<ProductVariant, Size> sizeJoin = productVariantJoin.join("size", JoinType.LEFT);
            // Join đến productColors -> color
            Join<ProductColor, Color> colorJoin = productColorJoin.join("color", JoinType.LEFT);

            // Kiểm tra xem Product và ProductColor có đang isActive
            Integer outOfStock = 0;
            predicates.add(cb.isTrue(root.get("isActive")));
//            predicates.add(cb.greaterThan(productVariantJoin.get("stock"), outOfStock));

            // 1. Xử lý mutually exclusive giữa query và category
            if (StringUtils.hasText(queryParams.getQuery())) {
                // Nếu có query thì lọc theo tên sản phẩm hoặc tên danh mục
                Predicate productNameLike = cb.like(cb.lower(root.get("name")), "%" + queryParams.getQuery().toLowerCase() + "%");
                Predicate categoryNameLike = cb.like(cb.lower(categoryJoin.get("name")), "%" + queryParams.getQuery().toLowerCase() + "%");
                predicates.add(cb.or(productNameLike, categoryNameLike));
            } else if (StringUtils.hasText(queryParams.getCategory())) {
                // Nếu có category & không phải "all" thì lọc theo codeName category
                if (!"all".equalsIgnoreCase(queryParams.getCategory())) {
                    predicates.add(cb.equal(cb.lower(categoryJoin.get("codeName")), queryParams.getCategory().toLowerCase()));
                }
                // Nếu category là "all" thì không thêm điều kiện nào
            }

            // 2. Lọc theo price (danh sách min-max)
            if (queryParams.getPrice() != null && !queryParams.getPrice().isEmpty()) {
                double globalMin = Double.MAX_VALUE;
                double globalMax = Double.MIN_VALUE;
                for (String range : queryParams.getPrice()) {
                    String[] parts = range.split("-");
                    double min = Double.parseDouble(parts[0]);
                    double max = parts[1].equalsIgnoreCase("infinity") ? Double.MAX_VALUE : Double.parseDouble(parts[1]);
                    globalMin = Math.min(globalMin, min);
                    globalMax = Math.max(globalMax, max);
                }
                predicates.add(cb.between(productColorJoin.get("price"), globalMin, globalMax));
            }

            // 3. Lọc theo Size (danh sách codeName của Size)
            if (queryParams.getSize() != null && !queryParams.getSize().isEmpty()) {
                predicates.add(sizeJoin.get("codeName").in(queryParams.getSize()));
            }

            // 4. Lọc theo Color (danh sách codeName của Color)
            if (queryParams.getColor() != null && !queryParams.getColor().isEmpty()) {
                predicates.add(colorJoin.get("codeName").in(queryParams.getColor()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Tạo PageRequest từ các tham số
        Sort sort = SearchParamsUtils.getSortParams(queryParams.getSortOrder());
        Pageable pageable = PageRequest.of(queryParams.getPageNumber(), queryParams.getPageSize(), sort);

        var productPage = productRepository.findAll(spec, pageable);

        return productPage.getContent().stream()
                .map(this::toProductResponseModel)
                .collect(Collectors.toList());
    }

    @QueryHandler
    public ProductResponseModel handle(GetProductQuery queryParams) {
        log.info("Get products detail with query, filter");

        var productDetail = productRepository.findById(queryParams.getProductId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        return toProductResponseModel(productDetail);
    }

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
                        .map(productColor ->
                                ProductResponseModel.ProductColor.builder()
                                        .id(productColor.getId())
                                        .price(productColor.getPrice())
                                        .isActive(productColor.getIsActive())
                                        .color(ProductResponseModel.Color.builder()
                                                .id(productColor.getColor().getId())
                                                .name(productColor.getColor().getName())
                                                .codeName(productColor.getColor().getCodeName())
                                                .colorCode(productColor.getColor().getCodeName())
                                                .description(productColor.getColor().getDescription())
                                                .isActive(productColor.getColor().getIsActive())
                                                .build())
                                        .promotions(productColor.getPromotions().stream().map(promotion ->
                                                ProductResponseModel.Promotion.builder()
                                                        .id(promotion.getId())
                                                        .name(promotion.getName())
                                                        .codeName(promotion.getCodeName())
                                                        .discountPercentage(promotion.getDiscountPercentage())
                                                        .startDate(promotion.getStartDate())
                                                        .endDate(promotion.getEndDate())
                                                        .isActive(promotion.getIsActive())
                                                        .build()).collect(Collectors.toList()))
                                        .productVariants(productColor.getProductVariants().stream().map(productVariant ->
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
                                        .build()).collect(Collectors.toList()))
                .build();
    }
}
