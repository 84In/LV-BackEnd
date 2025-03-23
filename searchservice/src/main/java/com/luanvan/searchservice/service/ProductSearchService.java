package com.luanvan.searchservice.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.json.JsonData;
import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.request.ProductImagesUploadModel;
import com.luanvan.commonservice.queries.GetAllProductWithFilterQuery;
import com.luanvan.searchservice.entity.ProductDocument;
import com.luanvan.searchservice.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.client.elc.*;
import  co. elastic. clients. elasticsearch._types.query_dsl.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchService {
    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductSearchRepository productSearchRepository;

    public Page<ProductDocument> searchProductsWithFilter(GetAllProductWithFilterQuery queryParams) {
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
        List<ProductDocument> products = searchHits.map(SearchHit::getContent).toList();

        return new PageImpl<>(products, pageable, searchHits.getTotalHits());
    }

    @KafkaListener(topics = "product-images-uploaded-topic", groupId = "product-group")
    public void uploadedProductImages(ProductImagesUploadModel message) {

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
}
