package com.luanvan.userservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetColorQuery;
import com.luanvan.userservice.query.model.CartResponseModel;
import com.luanvan.userservice.query.queries.GetAllUserQuery;
import com.luanvan.userservice.query.queries.GetCartQuery;
import com.luanvan.userservice.repository.CartRepository;
import com.luanvan.userservice.repository.UserRepository;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
public class CartQueryController {

    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ApiResponse<CartResponseModel> getDetail(String username) {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));

        GetCartQuery query = new GetCartQuery(username);

        CartResponseModel response = queryGateway.query(query, ResponseTypes.instanceOf(CartResponseModel.class)).join();

        return ApiResponse.<CartResponseModel>builder()
                .data(response)
                .build();
    }
}
