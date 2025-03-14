package com.luanvan.userservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.ApiResponse;
import com.luanvan.commonservice.model.response.UserResponseModel;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.userservice.query.model.PageUserResponse;
import com.luanvan.userservice.query.queries.GetAllUserQuery;
import com.luanvan.userservice.repository.UserRepository;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserQueryController {

    @Autowired
    private QueryGateway queryGateway;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ApiResponse<Page<UserResponseModel>> getAllUsers(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String sortBy = "createdAt";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().get().findFirst().orElse(null);
            if (order != null) {
                sortBy = order.getProperty();
                sortDirection = order.getDirection();
            }
        }
        GetAllUserQuery query = new GetAllUserQuery(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortBy,
                sortDirection
        );

        PageUserResponse response = queryGateway
                .query(query, ResponseTypes.instanceOf(PageUserResponse.class))
                .exceptionally((ex) -> {
                    throw new AppException(ErrorCode.QUERY_ERROR);
                })
                .join();

        Page<UserResponseModel> page = new PageImpl<UserResponseModel>(response.getContent(), PageRequest.of(response.getPageNumber(), response.getPageSize()), response.getTotalElements());
        return ApiResponse.<Page<UserResponseModel>>builder()
                .data(page)
                .build();

    }

    @GetMapping("/{username}")
    public ApiResponse<UserResponseModel> getUser(@PathVariable String username) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        GetUserQuery query = new GetUserQuery(username);

        UserResponseModel response = queryGateway.query(query, ResponseTypes.instanceOf(UserResponseModel.class)).join();

        return ApiResponse.<UserResponseModel>builder()
                .data(response)
                .build();
    }
}
