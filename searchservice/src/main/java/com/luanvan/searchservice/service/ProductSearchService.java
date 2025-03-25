package com.luanvan.searchservice.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.command.CallBackUploadProductImagesCommand;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.queries.GetAllProductWithFilterQuery;
import com.luanvan.commonservice.utils.PromotionUtils;
import com.luanvan.searchservice.entity.ProductDocument;
import com.luanvan.searchservice.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductSearchRepository productSearchRepository;

    @Cacheable(value = "products", key = "'productSearch:' + #queryParams.pageNumber + ':' + #queryParams.pageSize + ':' + #queryParams.query + ':' + #queryParams.category + ':' + #queryParams.price + ':' + #queryParams.color + ':' + #queryParams.size + ':' + #queryParams.sortOrder")
    public Page<ProductResponseModel> searchProductsWithFilter(GetAllProductWithFilterQuery queryParams) {
        log.info("Search products with filter elasticsearch");
        Pageable pageable = PageRequest.of(queryParams.getPageNumber(), queryParams.getPageSize());

        // Xây dựng BoolQuery bằng Elasticsearch Java Client
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // 1. Lọc sản phẩm có isActive = true
        boolQuery.must(m -> m.term(t -> t.field("isActive").value(true)));

        // 2. Lọc sản phẩm có category.isActive = true
        boolQuery.must(m -> m.term(t -> t.field("category.isActive").value(true)));

        // 3. Lọc theo từ khóa (query)
        if (StringUtils.hasText(queryParams.getQuery())) {
            boolQuery.must(m -> m.bool(b -> b
                    .should(s -> s.matchPhrasePrefix(mp -> mp.field("name").query(queryParams.getQuery())))
                    .should(s -> s.matchPhrasePrefix(mp -> mp.field("category.name").query(queryParams.getQuery())))
                    .should(s -> s.match(mq -> mq.field("name").query(queryParams.getQuery()).fuzziness("AUTO")))
                    .should(s -> s.match(mq -> mq.field("category.name").query(queryParams.getQuery()).fuzziness("AUTO")))
            ));
        }


        // 4. Lọc theo category
        if (StringUtils.hasText(queryParams.getCategory()) && !"all".equalsIgnoreCase(queryParams.getCategory())) {
            boolQuery.must(m -> m.term(t -> t.field("category.codeName").value(queryParams.getCategory())));
        }

        // 5. Lọc theo size (Nested Query) - Đã sửa
        if (StringUtils.hasText(queryParams.getSize())) {
            List<String> sizeList = Arrays.stream(queryParams.getSize().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            if (!sizeList.isEmpty()) {
                boolQuery.must(m -> m.nested(n -> n
                        .path("productColors.productVariants")
                        .query(q -> q.bool(b -> b
                                .must(mm -> mm.terms(t -> t
                                        .field("productColors.productVariants.size.codeName")
                                        .terms(tt -> tt.value(sizeList.stream().map(FieldValue::of).toList()))
                                ))
                        ))
                ));
            }
        }

        // 6. Lọc theo color (Nested Query) - Đã sửa
        if (StringUtils.hasText(queryParams.getColor())) {
            List<String> colorList = Arrays.stream(queryParams.getColor().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            if (!colorList.isEmpty()) {
                boolQuery.must(m -> m.nested(n -> n
                        .path("productColors")
                        .query(q -> q.bool(b -> b
                                .must(mm -> mm.terms(t -> t
                                        .field("productColors.color.codeName")
                                        .terms(tt -> tt.value(colorList.stream().map(FieldValue::of).toList()))
                                ))
                        ))
                ));
            }
        }

        // 7. Lọc theo price (min-max) với nhiều khoảng giá (Nested Query)
        if (StringUtils.hasText(queryParams.getPrice())) {
            String[] priceRanges = queryParams.getPrice().split(",");
            BoolQuery.Builder priceBoolBuilder = new BoolQuery.Builder();
            for (String range : priceRanges) {
                String[] parts = range.split("-");
                if (parts.length == 2) {
                    try {
                        BigDecimal minPrice = new BigDecimal(parts[0].trim());
                        BigDecimal maxPrice = parts[1].trim().equalsIgnoreCase("infinity")
                                ? BigDecimal.valueOf(Double.MAX_VALUE)
                                : new BigDecimal(parts[1].trim());
                        priceBoolBuilder.should(s -> s.range(RangeQuery.of(r ->
                                r.untyped(nr -> nr
                                        .field("productColors.price")
                                        .gte(JsonData.of(minPrice))
                                        .lte(JsonData.of(maxPrice))
                                ))));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Lỗi định dạng số trong price: " + range);
                    }
                }
            }
            BoolQuery priceBool = priceBoolBuilder.build();
            if (priceBool.should() != null && !priceBool.should().isEmpty()) {
                boolQuery.must(m -> m.nested(n -> n
                        .path("productColors")
                        .query(q -> q.bool(priceBool))
                ));
            }
        }

        // 8. Sắp xếp: Ví dụ, theo price, sold hoặc createdAt
        List<SortOptions> sortOptions = new ArrayList<>();
        if (StringUtils.hasText(queryParams.getSortOrder())) {
            // Chuyển đổi về chữ thường để so sánh không phân biệt hoa thường
            String sortOrder = queryParams.getSortOrder().toLowerCase();

            switch (sortOrder) {
                case "price-asc" -> sortOptions.add(
                        SortOptions.of(s -> s.field(f -> f
                                .field("productColors.finalPrice")
                                .order(SortOrder.Asc)
                                .nested(n -> n
                                        .path("productColors")
                                        // Chỉ định mode là min để lấy giá thấp nhất khi sắp xếp
                                        .filter(fq -> fq.term(t -> t.field("productColors.isActive").value(true)))
                                )
                        ))
                );
                case "price-desc" -> sortOptions.add(
                        SortOptions.of(s -> s.field(f -> f
                                .field("productColors.finalPrice")
                                .order(SortOrder.Desc)
                                .nested(n -> n
                                        .path("productColors")
                                        // Chỉ định mode là max để lấy giá cao nhất khi sắp xếp
                                        .filter(fq -> fq.term(t -> t.field("productColors.isActive").value(true)))
                                )
                        ))
                );
                case "sold-desc" -> sortOptions.add(
                        SortOptions.of(s -> s.field(f -> f
                                .field("productColors.productVariants.sold")
                                .order(SortOrder.Desc)
                                .nested(n -> n
                                        .path("productColors.productVariants")
                                        .filter(fq -> fq.term(t -> t.field("productColors.productVariants.isActive").value(true)))
                                )
                        ))
                );
                default -> sortOptions.add(
                        SortOptions.of(s -> s.field(f -> f.field("createdAt").order(SortOrder.Desc)))
                );
            }
        } else {
            sortOptions.add(SortOptions.of(s -> s.field(f -> f.field("createdAt").order(SortOrder.Desc))));
        }

        // 9. Tạo truy vấn NativeQuery
        NativeQuery nativeSearchQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.bool(boolQuery.build())))
                .withPageable(pageable)
                .withSort(sortOptions)
                .build();

        // 10. Thực thi truy vấn
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(nativeSearchQuery, ProductDocument.class);
        List<ProductResponseModel> products = searchHits
                .map(SearchHit::getContent)
                .map(this::toProductResponseModel)
                .toList();

        return new PageImpl<>(products, pageable, searchHits.getTotalHits());
    }

    @KafkaListener(topics = "product-images-uploaded-topic", groupId = "product-search-group")
    public void uploadedProductImages(CallBackUploadProductImagesCommand message) {

        log.info("Received product images upload event for productId: {} with URLs: {}", message.getProductId(), String.join(",", message.getImageUrls()));

        try {
            ProductDocument product = productSearchRepository.findById(message.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
            product.setImages(String.join(",", message.getImageUrls()));
            productSearchRepository.save(product);
            log.info("Product images URL updated for productId: {}", message.getProductId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public ProductResponseModel toProductResponseModel(ProductDocument product) {
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
