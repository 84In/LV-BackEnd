package com.luanvan.userservice.command.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.queries.GetProductQuery;
import com.luanvan.userservice.command.command.AddToCartCommand;
import com.luanvan.userservice.command.command.CreateCartCommand;
import com.luanvan.userservice.command.command.UpdateCartCommand;
import com.luanvan.userservice.command.model.CartCreateModel;
import com.luanvan.userservice.command.model.CartUpdateModel;
import com.luanvan.userservice.entity.Cart;
import com.luanvan.userservice.repository.CartDetailRepository;
import com.luanvan.userservice.repository.CartRepository;
import com.luanvan.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class CartCommandService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartDetailRepository cartDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private CommandGateway commandGateway;

    public HashMap<?, ?> create(CartCreateModel model) {
        log.info("Cart created");

        var user = userRepository.findByUsername(model.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        GetProductQuery query = new GetProductQuery(model.getCartDetail().getProductId());

        var product = queryGateway.query(query, ResponseTypes.instanceOf(ProductResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
                }).join();

        var productColor = product.getProductColors().stream()
                .filter(pc -> Objects.equals(pc.getColor().getId(), model.getCartDetail().getColorId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));

        var productVariant = productColor.getProductVariants().stream()
                .filter(pv -> Objects.equals(pv.getSize().getId(), model.getCartDetail().getSizeId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));

        // Nếu cart tồn tại thì tìm theo username, nếu chưa thì tạo mới
        Cart cart;
        Object command;
        if (!cartRepository.existsByUser(user)) {
            cart = Cart.builder()
                    .id(UUID.randomUUID().toString())
                    .user(user)
                    .cartDetails(new ArrayList<>())
                    .build();
            command = CreateCartCommand.builder()
                    .id(cart.getId())
                    .username(user.getUsername())
                    .cartDetail(CreateCartCommand.CartDetail.builder()
                            .id(UUID.randomUUID().toString())
                            .quantity(model.getCartDetail().getQuantity())
                            .productId(model.getCartDetail().getProductId())
                            .colorId(model.getCartDetail().getColorId())
                            .sizeId(model.getCartDetail().getSizeId())
                            .build())
                    .build();
        } else {

            cart = cartRepository.findByUser(user).get();
            var cartDetail = cart.getCartDetails().stream()
                    .filter(cd -> cd.getProductId().equals(model.getCartDetail().getProductId())
                            && cd.getColorId().equals(model.getCartDetail().getColorId())
                            && cd.getSizeId().equals(model.getCartDetail().getSizeId()))
                    .findFirst();
            // Nếu cart đã tồn tại và cartDetail cũng tồn tại thì cộng đồn số lượng
            if(cartDetail.isPresent()) {
                command = AddToCartCommand.builder()
                        .id(cart.getId())
                        .username(user.getUsername())
                        .cartDetail(AddToCartCommand.CartDetail.builder()
                                .id(cartDetail.get().getId())
                                .quantity(cartDetail.get().getQuantity() + model.getCartDetail().getQuantity())
                                .productId(cartDetail.get().getProductId())
                                .colorId(cartDetail.get().getColorId())
                                .sizeId(cartDetail.get().getSizeId())
                                .build())
                        .build();
            }else{
                // Nếu cart đã tồn tại và cartDetail chưa tồn tại thì ta mới cartDetailz
                command = AddToCartCommand.builder()
                        .id(cart.getId())
                        .username(user.getUsername())
                        .cartDetail(AddToCartCommand.CartDetail.builder()
                                .id(UUID.randomUUID().toString())
                                .quantity(model.getCartDetail().getQuantity())
                                .productId(model.getCartDetail().getProductId())
                                .colorId(model.getCartDetail().getColorId())
                                .sizeId(model.getCartDetail().getSizeId())
                                .build())
                        .build();
            }
        }

        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?, ?> update(CartUpdateModel model) {
        log.info("Cart updated");

        var user = userRepository.findByUsername(model.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        GetProductQuery query = new GetProductQuery(model.getCartDetail().getProductId());

        var product = queryGateway.query(query, ResponseTypes.instanceOf(ProductResponseModel.class))
                .exceptionally(ex -> {
                    throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
                }).join();

        var productColorOpt = product.getProductColors().stream()
                .filter(pc -> Objects.equals(pc.getColor().getId(), model.getCartDetail().getColorId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));

        var productVariantOpt = productColorOpt.getProductVariants().stream()
                .filter(pv -> Objects.equals(pv.getSize().getId(), model.getCartDetail().getSizeId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));

        // Nếu cart tồn tại thì tìm theo username
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        var cartDetail = cart.getCartDetails().stream()
                .filter(cd -> cd.getId().equals(model.getCartDetail().getId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_DETAIL_NOT_EXISTED));

        UpdateCartCommand command = UpdateCartCommand.builder()
                .id(cart.getId())
                .username(user.getUsername())
                .cartDetail(UpdateCartCommand.CartDetail.builder()
                        .id(cartDetail.getId())
                        .quantity(model.getCartDetail().getQuantity())
                        .productId(model.getCartDetail().getProductId())
                        .colorId(model.getCartDetail().getColorId())
                        .sizeId(model.getCartDetail().getSizeId())
                        .build())
                .build();

        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

}
