package com.luanvan.userservice.query.projection;

import com.luanvan.commonservice.model.RoleResponse;
import com.luanvan.commonservice.model.UserAddressResponse;
import com.luanvan.commonservice.model.UserResponseModel;
import com.luanvan.userservice.entity.Address;
import com.luanvan.userservice.entity.User;
import com.luanvan.userservice.query.queries.GetAllUserQuery;
import com.luanvan.userservice.query.queries.GetUserQuery;
import com.luanvan.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserProjection {

    @Autowired
    private UserRepository userRepository;

    @QueryHandler
    public List<UserResponseModel> handle(GetAllUserQuery query) {
        log.info("handle get all users");

        // Lấy tất cả users từ repository
        List<User> users = userRepository.findAll();
        log.info(users.toString());
        log.info("handle get users success");

        // Chuyển đổi danh sách users sang UserResponseModel
        return users.stream().map(user -> {
            // Tạo UserResponseModel
            UserResponseModel userResponseModel = new UserResponseModel();
            userResponseModel.setId(user.getId());
            userResponseModel.setUsername(user.getUsername());
            userResponseModel.setPassword(user.getPassword());
            userResponseModel.setEmail(user.getEmail());
            userResponseModel.setPhone(user.getPhone());
            userResponseModel.setFirstName(user.getFirstName());
            userResponseModel.setLastName(user.getLastName());
            userResponseModel.setAvatar(user.getAvatar());
            userResponseModel.setActive(user.getActive());

            // Mapping Role
            userResponseModel.setRole(new RoleResponse(user.getRole().getName(), user.getRole().getDescription()));

            // Mapping UserAddressResponse
            userResponseModel.setAddresses(user.getAddresses().stream().map(userAddress -> {
                UserAddressResponse userAddressResponse = new UserAddressResponse();
                userAddressResponse.setAddressId(userAddress.getId().getAddressId());
                userAddressResponse.setUserId(userAddress.getId().getUserId());

                // Lấy thông tin từ Address và mapping vào UserAddressResponse
                Address address = userAddress.getAddress();
                userAddressResponse.setHouseNumberAndStreet(address.getHouseNumberAndStreet());
                userAddressResponse.setAddressPhone(address.getPhone());
                userAddressResponse.setProvinceName(address.getProvince().getName());
                userAddressResponse.setDistrictName(address.getDistrict().getName());
                userAddressResponse.setWardName(address.getWard() != null ? address.getWard().getName() : null);
                userAddressResponse.setDefault(userAddress.isDefault());
                userAddressResponse.setCreatedAt(userAddress.getCreatedAt());
                userAddressResponse.setUpdatedAt(userAddress.getUpdatedAt());

                return userAddressResponse;
            }).collect(Collectors.toList())); // Thu thập thành danh sách

            // Mapping thời gian tạo và cập nhật
            userResponseModel.setCreatedAt(user.getCreatedAt());
            userResponseModel.setUpdatedAt(user.getUpdatedAt());

            log.info("handle get all users dto");
            log.info(userResponseModel.toString());

            return userResponseModel;
        }).collect(Collectors.toList()); // Thu thập thành danh sách
    }

    @QueryHandler
    public UserResponseModel handle(GetUserQuery query){
        User user = userRepository.findByUsername(query.getUsername()).orElseThrow( ()-> new RuntimeException("Not found user"));
        UserResponseModel userResponseModel = new UserResponseModel();
        userResponseModel.setId(user.getId());
        userResponseModel.setUsername(user.getUsername());
        userResponseModel.setPassword(user.getPassword());
        userResponseModel.setEmail(user.getEmail());
        userResponseModel.setPhone(user.getPhone());
        userResponseModel.setFirstName(user.getFirstName());
        userResponseModel.setLastName(user.getLastName());
        userResponseModel.setAvatar(user.getAvatar());
        userResponseModel.setActive(user.getActive());

        // Mapping Role
        userResponseModel.setRole(new RoleResponse(user.getRole().getName(), user.getRole().getDescription()));

        // Mapping UserAddressResponse
        userResponseModel.setAddresses(user.getAddresses().stream().map(userAddress -> {
            UserAddressResponse userAddressResponse = new UserAddressResponse();
            userAddressResponse.setAddressId(userAddress.getId().getAddressId());
            userAddressResponse.setUserId(userAddress.getId().getUserId());

            // Lấy thông tin từ Address và mapping vào UserAddressResponse
            Address address = userAddress.getAddress();
            userAddressResponse.setHouseNumberAndStreet(address.getHouseNumberAndStreet());
            userAddressResponse.setAddressPhone(address.getPhone());
            userAddressResponse.setProvinceName(address.getProvince().getName());
            userAddressResponse.setDistrictName(address.getDistrict().getName());
            userAddressResponse.setWardName(address.getWard() != null ? address.getWard().getName() : null);
            userAddressResponse.setDefault(userAddress.isDefault());
            userAddressResponse.setCreatedAt(userAddress.getCreatedAt());
            userAddressResponse.setUpdatedAt(userAddress.getUpdatedAt());

            return userAddressResponse;
        }).collect(Collectors.toList())); // Thu thập thành danh sách

        // Mapping thời gian tạo và cập nhật
        userResponseModel.setCreatedAt(user.getCreatedAt());
        userResponseModel.setUpdatedAt(user.getUpdatedAt());
        return userResponseModel;
    }
}
