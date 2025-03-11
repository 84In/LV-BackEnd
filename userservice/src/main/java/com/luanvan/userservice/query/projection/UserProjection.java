package com.luanvan.userservice.query.projection;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.*;
import com.luanvan.commonservice.queries.GetUserDetailQuery;
import com.luanvan.commonservice.queries.GetUserQuery;
import com.luanvan.userservice.entity.Address;
import com.luanvan.userservice.entity.User;
import com.luanvan.userservice.entity.UserAddress;
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

import java.util.Comparator;
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
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), Sort.by(query.getSortDirection(), query.getSortBy()));
        var userPage = userRepository.findAll(pageable);
        return userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .peek(dto -> {
                    log.info("Mapped user DTO: {}", dto);
                })
                .collect(Collectors.toList());
    }

    @QueryHandler
    public UserResponseModel handle(GetUserQuery query) {
        User user = userRepository.findByUsername(query.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return mapToUserResponse(user);
    }

    @QueryHandler
    public UserResponseModel handle(GetUserDetailQuery query) {
        User user = userRepository.findById(query.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return mapToUserResponse(user);
    }

    // Helper method: Mapping User entity to UserResponseModel DTO
    private UserResponseModel mapToUserResponse(User user) {
        return UserResponseModel.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatar(user.getAvatar())
                .active(user.getActive())
                .role(mapToRoleResponse(user))
                .addresses(mapAddressResponse(user))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // Helper method: Mapping Role from User entity
    private RoleResponseModel mapToRoleResponse(User user) {
        return new RoleResponseModel(user.getRole().getName(), user.getRole().getDescription());
    }

    // Helper method: Mapping addresses
    private List<UserAddressResponseModel> mapAddressResponse(User user) {
        return user.getAddresses().stream()
                .sorted(Comparator.comparing(UserAddress::getCreatedAt).reversed())
                .filter(ad -> ad.getAddress().getIsActive().equals(Boolean.TRUE))
                .map(this::mapAddress)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Helper method: Mapping a single address
    private UserAddressResponseModel mapAddress(UserAddress userAddress) {
        if (userAddress == null || userAddress.getAddress() == null || !userAddress.getAddress().getIsActive()) {
            return null;
        }
        Address address = userAddress.getAddress();
        UserAddressResponseModel dto = new UserAddressResponseModel();
        dto.setAddressId(userAddress.getId().getAddressId());
        dto.setUserId(userAddress.getId().getUserId());
        dto.setName(address.getName());
        dto.setHouseNumberAndStreet(address.getHouseNumberAndStreet());
        dto.setPhone(address.getPhone());
        // Mapping Province
        ProvinceResponseModel province = new ProvinceResponseModel();
        BeanUtils.copyProperties(address.getProvince(), province);
        dto.setProvince(province);
        // Mapping District
        DistrictResponseModel district = new DistrictResponseModel();
        BeanUtils.copyProperties(address.getDistrict(), district);
        dto.setDistrict(district);
        // Mapping Ward if exists
        if (address.getWard() != null) {
            WardResponseModel ward = new WardResponseModel();
            BeanUtils.copyProperties(address.getWard(), ward);
            dto.setWard(ward);
        } else {
            dto.setWard(null);
        }
        dto.setIsDefault(userAddress.isDefault());
        dto.setCreatedAt(userAddress.getCreatedAt());
        dto.setUpdatedAt(userAddress.getUpdatedAt());
        return dto;
    }
}
