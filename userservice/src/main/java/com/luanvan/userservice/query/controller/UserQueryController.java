package com.luanvan.userservice.query.controller;

import com.luanvan.userservice.query.model.UserResponseModel;
import com.luanvan.userservice.query.queries.GetAllUserQuery;
import com.luanvan.userservice.query.queries.GetUserQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<UserResponseModel> getAllUsers() {
        GetAllUserQuery getAllUserQuery = new GetAllUserQuery();
        return queryGateway.query(getAllUserQuery, ResponseTypes.multipleInstancesOf(UserResponseModel.class)).exceptionally(ex -> {
            throw new RuntimeException("Query failed", ex);
        }).join();
    }

    @GetMapping("{username}")
    public UserResponseModel getUserByUsername(@PathVariable String username) {
        GetUserQuery getUserQuery = new GetUserQuery(username);
        return queryGateway.query(getUserQuery, ResponseTypes.instanceOf(UserResponseModel.class)).exceptionally(ex -> {
            throw new RuntimeException("Query failed", ex);
        }).join();
    }
}
