package com.luanvan.userservice.query.projection;

import com.luanvan.userservice.entity.User;
import com.luanvan.userservice.query.model.UserResponseModel;
import com.luanvan.userservice.query.queries.GetAllUserQuery;
import com.luanvan.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserProjection {

    @Autowired
    private UserRepository userRepository;

    @QueryHandler
    public List<UserResponseModel> handle(GetAllUserQuery query){
        Pageable pageable = PageRequest.of(
                query.getPage(),
                query.getSize(),
                Sort.by(query.getSortDirection(), query.getSortBy())
        );

        var userPages = userRepository.findAll(pageable);

        return userPages
                .getContent()
                .stream()
                .map(
                        user -> {
                            UserResponseModel userResponseModel = UserResponseModel.builder()
                                    .id(user.getId())
                                    .username(user.getUsername())
                                    .password(user.getPassword())
                                    .email(user.getEmail())
                                    .phone(user.getPhone())
                                    .firstName(user.getFirstName())
                                    .lastName(user.getLastName())
                                    .avatar(user.getAvatar())
                                    .active(user.getActive())
                                    .role(user.getRole())
                                    .addresses(user.getAddresses())
                                    .createdAt(user.getCreatedAt())
                                    .updatedAt(user.getUpdatedAt())
                                    .build();
                            log.info("handle get all users dto");
                            log.info(userResponseModel.toString());
                            return userResponseModel;
                        }
                ).collect(Collectors.toList());
    }
}
