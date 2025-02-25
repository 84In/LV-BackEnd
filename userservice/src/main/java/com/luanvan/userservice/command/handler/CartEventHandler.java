package com.luanvan.userservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.userservice.command.event.CartAddToEvent;
import com.luanvan.userservice.command.event.CartCreatedEvent;
import com.luanvan.userservice.command.event.CartUpdatedEvent;
import com.luanvan.userservice.entity.Cart;
import com.luanvan.userservice.entity.CartDetail;
import com.luanvan.userservice.repository.CartRepository;
import com.luanvan.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Component
public class CartEventHandler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private CartRepository cartRepository;

    @EventHandler
    @Transactional
    public void on(CartCreatedEvent event) {
        log.info("Cart created");

        // 1. Lấy user
        var user = userRepository.findByUsername(event.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Lấy thông tin sản phẩm qua query
        var product = queryGateway.query(
                new GetProductQuery(event.getCartDetail().getProductId()),
                ResponseTypes.instanceOf(ProductResponseModel.class)
        ).exceptionally(ex -> {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }).join();

        // 3. Lấy productColor và productVariant (không lặp filter)
        var productColorOpt = product.getProductColors().stream()
                .filter(pc -> Objects.equals(pc.getColor().getId(), event.getCartDetail().getColorId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));

        var productVariantOpt = productColorOpt.getProductVariants().stream()
                .filter(pv -> Objects.equals(pv.getSize().getId(), event.getCartDetail().getSizeId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));

        // 4. Tìm cart theo user, nếu chưa có thì tạo
        var cart = cartRepository.findByUser(user)
                .orElseGet(() -> Cart.builder()
                        .id(event.getId())
                        .user(user)
                        .cartDetails(new ArrayList<>())
                        .build());

        // 5. Xử lý số lượng (requestQuantity vs stockQuantity)
        var stockQuantity = productVariantOpt.getStock();
        var requestQuantity = event.getCartDetail().getQuantity();

        // 6. Tìm cartDetail đã có hay chưa
        var cartDetailOpt = cart.getCartDetails().stream()
                .filter(cd -> cd.getProductId().equals(product.getId())
                        && cd.getColorId().equals(productColorOpt.getColor().getId())
                        && cd.getSizeId().equals(productVariantOpt.getSize().getId()))
                .findFirst();

        if (cartDetailOpt.isPresent()) {
            // Đã tồn tại, cộng dồn
            var cartDetail = cartDetailOpt.get();
            int newQuantity = cartDetail.getQuantity() + requestQuantity;
            cartDetail.setQuantity(Math.min(newQuantity, stockQuantity));
        } else {
            // Tạo mới
            int finalQuantity = Math.min(requestQuantity, stockQuantity);
            var newDetail = CartDetail.builder()
                    .id(event.getCartDetail().getId())
                    .quantity(finalQuantity)
                    .productId(product.getId())
                    .colorId(productColorOpt.getColor().getId())
                    .sizeId(productVariantOpt.getSize().getId())
                    .cart(cart)
                    .build();
            cart.getCartDetails().add(newDetail);
        }

        // 7. Lưu cart
        cartRepository.save(cart);
    }

    @EventHandler
    @Transactional
    public void on(CartAddToEvent event) {
        log.info("Add to cart");

        // 1. Lấy user
        var user = userRepository.findByUsername(event.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Lấy thông tin sản phẩm qua query
        var product = queryGateway.query(
                new GetProductQuery(event.getCartDetail().getProductId()),
                ResponseTypes.instanceOf(ProductResponseModel.class)
        ).exceptionally(ex -> {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }).join();

        // 3. Lấy productColor và productVariant (không lặp filter)
        var productColorOpt = product.getProductColors().stream()
                .filter(pc -> Objects.equals(pc.getColor().getId(), event.getCartDetail().getColorId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));

        var productVariantOpt = productColorOpt.getProductVariants().stream()
                .filter(pv -> Objects.equals(pv.getSize().getId(), event.getCartDetail().getSizeId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));

        // 4. Tìm cart theo user
        var cart = cartRepository.findByUser(user).get();

        // 5. Xử lý số lượng (requestQuantity vs stockQuantity)
        var stockQuantity = productVariantOpt.getStock();
        var requestQuantity = event.getCartDetail().getQuantity();

        // 6. Tìm cartDetail đã có hay chưa
        var cartDetailOpt = cart.getCartDetails().stream()
                .filter(cd -> cd.getProductId().equals(product.getId())
                        && cd.getColorId().equals(productColorOpt.getColor().getId())
                        && cd.getSizeId().equals(productVariantOpt.getSize().getId()))
                .findFirst();

        if (cartDetailOpt.isPresent()) {
            // Đã tồn tại, cập nhật số lượng
            var cartDetail = cartDetailOpt.get();
            int newQuantity = requestQuantity;
            cartDetail.setQuantity(Math.min(newQuantity, stockQuantity));
        } else {
            // Tạo mới
            int finalQuantity = Math.min(requestQuantity, stockQuantity);
            var newDetail = CartDetail.builder()
                    .id(event.getCartDetail().getId())
                    .quantity(finalQuantity)
                    .productId(product.getId())
                    .colorId(productColorOpt.getColor().getId())
                    .sizeId(productVariantOpt.getSize().getId())
                    .cart(cart)
                    .build();
            cart.getCartDetails().add(newDetail);
        }

        // 7. Lưu cart
        cartRepository.save(cart);
    }

    @EventHandler
    @Transactional
    public void on(CartUpdatedEvent event) {
        log.info("Cart update");

        // 1. Lấy user
        var user = userRepository.findByUsername(event.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // 2. Lấy thông tin sản phẩm qua query
        var product = queryGateway.query(
                new GetProductQuery(event.getCartDetail().getProductId()),
                ResponseTypes.instanceOf(ProductResponseModel.class)
        ).exceptionally(ex -> {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }).join();

        // 3. Lấy productColor và productVariant (không lặp filter)
        var productColorOpt = product.getProductColors().stream()
                .filter(pc -> Objects.equals(pc.getColor().getId(), event.getCartDetail().getColorId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));

        var productVariantOpt = productColorOpt.getProductVariants().stream()
                .filter(pv -> Objects.equals(pv.getSize().getId(), event.getCartDetail().getSizeId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));

        // 4. Tìm cart theo user
        var cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        // 5. Xử lý số lượng (requestQuantity vs stockQuantity)
        var stockQuantity = productVariantOpt.getStock();
        var requestQuantity = event.getCartDetail().getQuantity();

        // 6. Tìm cartDetail đã có hay chưa
        var cartDetailOpt = cart.getCartDetails().stream()
                .filter(cd -> cd.getId().equals(event.getCartDetail().getId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_DETAIL_NOT_EXISTED));
        // 7. Kiểm tra xem trong giỏ hàng có CartDetail nào khác có cùng (productId, colorId, sizeId)
        var existCartDetailByColorSize = cart.getCartDetails().stream()
                .filter(cd -> cd.getProductId().equals(product.getId())
                        && cd.getColorId().equals(productColorOpt.getColor().getId())
                        && cd.getSizeId().equals(productVariantOpt.getSize().getId())
                        && !Objects.equals(cd.getId(), cartDetailOpt.getId()))
                .findFirst();
        // Sản phẩm cập nhật đã tồn tại màu sắc trước đó thì xóa cái cũ cộng dồn số lượng sang cartDetail mới
        if (existCartDetailByColorSize.isPresent()) {
            // Nếu đã tồn tại một CartDetail khác thì cộng dồn số lượng:
            var otherDetail = existCartDetailByColorSize.get();
            int newQuantity = otherDetail.getQuantity() + requestQuantity;
            cartDetailOpt.setQuantity(Math.min(newQuantity, stockQuantity));
            cartDetailOpt.setProductId(product.getId());
            cartDetailOpt.setColorId(productColorOpt.getColor().getId());
            cartDetailOpt.setSizeId(productVariantOpt.getSize().getId());
            // Sau khi gộp, loại bỏ CartDetail cũ khỏi giỏ hàng
            cart.getCartDetails().remove(otherDetail);
            log.info("Update exist cart detail by color size has quantity {}", Math.min(newQuantity, stockQuantity));
        } else {
            // Không có CartDetail nào khác với combination mới, cập nhật thông tin cho detailToUpdate
            cartDetailOpt.setQuantity(Math.min(requestQuantity, stockQuantity));
            cartDetailOpt.setProductId(product.getId());
            cartDetailOpt.setColorId(productColorOpt.getColor().getId());
            cartDetailOpt.setSizeId(productVariantOpt.getSize().getId());
            log.info("Update new cart detail by color size: {} {}", productColorOpt.getColor().getId(), productVariantOpt.getSize().getId());
        }

        // 8. Lưu lại giỏ hàng
        cartRepository.save(cart);
    }

}
