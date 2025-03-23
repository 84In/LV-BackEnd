package com.luanvan.searchservice.repository;

import com.luanvan.searchservice.entity.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    List<ProductDocument> findByCategoryId(String categoryId);

    List<ProductDocument> findByProductColorsColorId(String colorId);

    List<ProductDocument> findByProductColorsProductVariantsSizeId(String sizeId);

    List<ProductDocument> findByProductColorsPromotionId(String promotionId);
}
