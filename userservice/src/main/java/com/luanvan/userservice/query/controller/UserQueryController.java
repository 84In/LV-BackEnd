package com.luanvan.userservice.query.controller;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.ApiResponse;
import com.luanvan.userservice.query.model.UserResponseModel;
import com.luanvan.userservice.query.queries.GetAllUserQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserQueryController {

    @Autowired
    private QueryGateway queryGateway;

    @GetMapping
    public ApiResponse<Page<UserResponseModel>> getAllUsers(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {

        String sortBy = "createdAt";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (pageable.getSort().isSorted()){
            Sort.Order order = pageable.getSort().get().findFirst().orElse(null);
            if (order != null){
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

        List<UserResponseModel> response = queryGateway
                .query(query, ResponseTypes.multipleInstancesOf(UserResponseModel.class))
                .exceptionally((ex)->{throw  new AppException(ErrorCode.QUERY_ERROR);})
                .join();

        Page<UserResponseModel> page = new PageImpl<UserResponseModel>(response, pageable, response.size());
        return ApiResponse.<Page<UserResponseModel>>builder()
                .data(page)
                .build();

    }
}
