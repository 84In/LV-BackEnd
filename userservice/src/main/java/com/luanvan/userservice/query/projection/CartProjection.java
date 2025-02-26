package com.luanvan.userservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.userservice.entity.Cart;
import com.luanvan.userservice.entity.CartDetail;
import com.luanvan.userservice.query.model.CartResponseModel;
import com.luanvan.userservice.query.queries.GetCartQuery;
import com.luanvan.userservice.repository.CartRepository;
import com.luanvan.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CartProjection {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private QueryGateway queryGateway;

    @QueryHandler
    public CartResponseModel handle(GetCartQuery query) {
        log.info("Handling GetCartQuery for username: {}", query.getUsername());

        var user = userRepository.findByUsername(query.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        return toCartResponseModel(cart);
    }

    private CartResponseModel toCartResponseModel(Cart cart) {
        // Nhóm CartDetail theo productId để giảm số lần gọi QueryGateway
        var cartDetailsByProduct = cart.getCartDetails().stream()
                .collect(Collectors.groupingBy(CartDetail::getProductId));

        // Truy vấn sản phẩm cho mỗi productId một lần và lưu vào map
        var productMap = cartDetailsByProduct.keySet().stream()
                .collect(Collectors.toMap(
                        pid -> pid,
                        pid -> queryGateway.query(new GetProductQuery(pid),
                                        ResponseTypes.instanceOf(ProductResponseModel.class))
                                .exceptionally(ex -> {
                                    throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
                                })
                                .join()
                ));

        // Mapping CartDetail
        var cartDetailResponses = cart.getCartDetails().stream()
                .map(cd -> mapCartDetail(cd, productMap.get(cd.getProductId())))
                .collect(Collectors.toList());

        return CartResponseModel.builder()
                .id(cart.getId())
                .username(cart.getUser().getUsername())
                .cartDetails(cartDetailResponses)
                .build();
    }

    private CartResponseModel.CartDetail mapCartDetail(CartDetail cd, ProductResponseModel product) {
        // Lấy productColor theo colorId
        var productColor = product.getProductColors().stream()
                .filter(pc -> pc.getColor().getId().equals(cd.getColorId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_COLOR_NOT_EXISTED));

        var colorResponse = CartResponseModel.Color.builder()
                .id(productColor.getColor().getId())
                .name(productColor.getColor().getName())
                .codeName(productColor.getColor().getCodeName())
                .colorCode(productColor.getColor().getColorCode())
                .description(productColor.getColor().getDescription())
                .isActive(productColor.getColor().getIsActive())
                .build();

        // Lấy productVariant theo sizeId
        var productVariant = productColor.getProductVariants().stream()
                .filter(pv -> pv.getSize().getId().equals(cd.getSizeId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_EXISTED));

        var sizeResponse = CartResponseModel.Size.builder()
                .id(productVariant.getSize().getId())
                .name(productVariant.getSize().getName())
                .codeName(productVariant.getSize().getCodeName())
                .isActive(productVariant.getSize().getIsActive())
                .build();

        return CartResponseModel.CartDetail.builder()
                .id(cd.getId())
                .quantity(cd.getQuantity())
                .color(colorResponse)
                .size(sizeResponse)
                .product(toProductResponseModel(product))
                .createdAt(cd.getCreatedAt())
                .updatedAt(cd.getUpdatedAt())
                .build();
    }

    public CartResponseModel.Product toProductResponseModel(ProductResponseModel product) {
        return CartResponseModel.Product.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .images(product.getImages())
                .isActive(product.getIsActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .category(CartResponseModel.Category.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .codeName(product.getCategory().getCodeName())
                        .images(product.getCategory().getImages())
                        .description(product.getCategory().getDescription())
                        .isActive(product.getCategory().getIsActive())
                        .build())
                .productColors(product.getProductColors().stream()
                        .filter(pc -> Boolean.TRUE.equals(pc.getIsActive()))
                        .map(pc -> CartResponseModel.ProductColor.builder()
                                .id(pc.getId())
                                .price(pc.getPrice())
                                .finalPrice(pc.getFinalPrice())
                                .isActive(pc.getIsActive())
                                .color(CartResponseModel.Color.builder()
                                        .id(pc.getColor().getId())
                                        .name(pc.getColor().getName())
                                        .codeName(pc.getColor().getCodeName())
                                        .colorCode(pc.getColor().getColorCode())
                                        .description(pc.getColor().getDescription())
                                        .isActive(pc.getColor().getIsActive())
                                        .build())
                                .promotion(Optional.ofNullable(pc.getPromotion())
                                        .map(promo -> CartResponseModel.Promotion.builder()
                                                .id(promo.getId())
                                                .name(promo.getName())
                                                .codeName(promo.getCodeName())
                                                .discountPercentage(promo.getDiscountPercentage())
                                                .startDate(promo.getStartDate())
                                                .endDate(promo.getEndDate())
                                                .isActive(promo.getIsActive())
                                                .build())
                                        .orElse(null))
                                .productVariants(pc.getProductVariants().stream()
                                        .map(pv -> CartResponseModel.ProductVariant.builder()
                                                .id(pv.getId())
                                                .stock(pv.getStock())
                                                .sold(pv.getSold())
                                                .isActive(pv.getIsActive())
                                                .size(CartResponseModel.Size.builder()
                                                        .id(pv.getSize().getId())
                                                        .name(pv.getSize().getName())
                                                        .codeName(pv.getSize().getCodeName())
                                                        .isActive(pv.getSize().getIsActive())
                                                        .build())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

