package com.luanvan.userservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.*;
import com.luanvan.commonservice.queries.GetUserDetailQuery;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.userservice.entity.Address;
import com.luanvan.userservice.entity.User;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserProjection {

    @Autowired
    private UserRepository userRepository;

    @QueryHandler
    public List<UserResponseModel> handle(GetAllUserQuery query) {
        Pageable pageable = PageRequest.of(
                query.getPage(),
                query.getSize(),
                Sort.by(query.getSortDirection(), query.getSortBy())
        );

        var userPages = userRepository.findAll(pageable);

        return userPages.getContent().stream().map(user -> {

                    UserResponseModel userResponseModel = UserResponseModel.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .avatar(user.getAvatar())
                            .active(user.getActive())
                            .role(new RoleResponseModel(user.getRole().getName(), user.getRole().getDescription()))
                            .addresses(user.getAddresses().stream().map(userAddress -> {
                                UserAddressResponseModel userAddressResponse = new UserAddressResponseModel();
                                userAddressResponse.setAddressId(userAddress.getId().getAddressId());
                                userAddressResponse.setUserId(userAddress.getId().getUserId());

                                // Lấy thông tin từ Address và mapping vào UserAddressResponse
                                Address address = userAddress.getAddress();
                                userAddressResponse.setName(address.getName());
                                userAddressResponse.setHouseNumberAndStreet(address.getHouseNumberAndStreet());
                                userAddressResponse.setAddressPhone(address.getPhone());
                                ProvinceResponseModel provinceResponseModel = new ProvinceResponseModel();
                                BeanUtils.copyProperties(address.getProvince(), provinceResponseModel);
                                userAddressResponse.setProvince(provinceResponseModel);
                                DistrictResponseModel districtResponseModel = new DistrictResponseModel();
                                BeanUtils.copyProperties(address.getDistrict(),districtResponseModel);
                                userAddressResponse.setDistrict(districtResponseModel);
                                if(address.getWard() != null){
                                    WardResponseModel wardResponseModel = new WardResponseModel();
                                    BeanUtils.copyProperties(address.getWard(), wardResponseModel);
                                    userAddressResponse.setWard(wardResponseModel);
                                }else{
                                    userAddressResponse.setWard(null);
                                }
                                userAddressResponse.setDefault(userAddress.isDefault());
                                userAddressResponse.setCreatedAt(userAddress.getCreatedAt());
                                userAddressResponse.setUpdatedAt(userAddress.getUpdatedAt());

                                return userAddressResponse;
                            }).collect(Collectors.toList()))
                            .createdAt(user.getCreatedAt())
                            .updatedAt(user.getUpdatedAt())
                            .build();
                    log.info("handle get all users dto");
                    log.info(userResponseModel.toString());
                    return userResponseModel;
                }
        ).collect(Collectors.toList());
    }

    @QueryHandler
    public UserResponseModel handle(GetUserQuery query) {
        User user = userRepository.findByUsername(query.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

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
        userResponseModel.setRole(new RoleResponseModel(user.getRole().getName(), user.getRole().getDescription()));

        // Mapping UserAddressResponse
        userResponseModel.setAddresses(user.getAddresses().stream().map(userAddress -> {
            UserAddressResponseModel userAddressResponse = new UserAddressResponseModel();
            userAddressResponse.setAddressId(userAddress.getId().getAddressId());
            userAddressResponse.setUserId(userAddress.getId().getUserId());

            // Lấy thông tin từ Address và mapping vào UserAddressResponse
            Address address = userAddress.getAddress();
            userAddressResponse.setName(address.getName());
            userAddressResponse.setHouseNumberAndStreet(address.getHouseNumberAndStreet());
            userAddressResponse.setAddressPhone(address.getPhone());
            ProvinceResponseModel provinceResponseModel = new ProvinceResponseModel();
            BeanUtils.copyProperties(address.getProvince(), provinceResponseModel);
            userAddressResponse.setProvince(provinceResponseModel);
            DistrictResponseModel districtResponseModel = new DistrictResponseModel();
            BeanUtils.copyProperties(address.getDistrict(),districtResponseModel);
            userAddressResponse.setDistrict(districtResponseModel);
            if(address.getWard() != null){
                WardResponseModel wardResponseModel = new WardResponseModel();
                BeanUtils.copyProperties(address.getWard(), wardResponseModel);
                userAddressResponse.setWard(wardResponseModel);
            }else{
                userAddressResponse.setWard(null);
            }
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

    @QueryHandler
    public UserResponseModel handle(GetUserDetailQuery query) {
        User user = userRepository.findById(query.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserResponseModel userResponseModel = new UserResponseModel();
        userResponseModel.setId(user.getId());
        userResponseModel.setUsername(user.getUsername());
        userResponseModel.setEmail(user.getEmail());
        userResponseModel.setPhone(user.getPhone());
        userResponseModel.setFirstName(user.getFirstName());
        userResponseModel.setLastName(user.getLastName());
        userResponseModel.setAvatar(user.getAvatar());
        userResponseModel.setActive(user.getActive());

        // Mapping Role
        userResponseModel.setRole(new RoleResponseModel(user.getRole().getName(), user.getRole().getDescription()));

        // Mapping UserAddressResponse
        userResponseModel.setAddresses(user.getAddresses().stream().map(userAddress -> {


            if (userAddress.getAddress().getIsActive() == true) {
                UserAddressResponseModel userAddressResponse = new UserAddressResponseModel();
                userAddressResponse.setAddressId(userAddress.getId().getAddressId());
                userAddressResponse.setUserId(userAddress.getId().getUserId());

                // Lấy thông tin từ Address và mapping vào UserAddressResponse
                Address address = userAddress.getAddress();
                userAddressResponse.setName(address.getName());
                userAddressResponse.setHouseNumberAndStreet(address.getHouseNumberAndStreet());
                userAddressResponse.setAddressPhone(address.getPhone());
                ProvinceResponseModel provinceResponseModel = new ProvinceResponseModel();
                BeanUtils.copyProperties(address.getProvince(), provinceResponseModel);
                userAddressResponse.setProvince(provinceResponseModel);
                DistrictResponseModel districtResponseModel = new DistrictResponseModel();
                BeanUtils.copyProperties(address.getDistrict(),districtResponseModel);
                userAddressResponse.setDistrict(districtResponseModel);
                if(address.getWard() != null){
                    WardResponseModel wardResponseModel = new WardResponseModel();
                    BeanUtils.copyProperties(address.getWard(), wardResponseModel);
                    userAddressResponse.setWard(wardResponseModel);
                }else{
                    userAddressResponse.setWard(null);
                }
                userAddressResponse.setDefault(userAddress.isDefault());
                userAddressResponse.setCreatedAt(userAddress.getCreatedAt());
                userAddressResponse.setUpdatedAt(userAddress.getUpdatedAt());

                return userAddressResponse;
            }

            return null;
        }) .filter(Objects::nonNull).collect(Collectors.toList())); // Thu thập thành danh sách bỏ qua các giá trị not active

        // Mapping thời gian tạo và cập nhật
        userResponseModel.setCreatedAt(user.getCreatedAt());
        userResponseModel.setUpdatedAt(user.getUpdatedAt());
        return userResponseModel;
    }
}
