package com.luanvan.userservice.repository;

import com.luanvan.userservice.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UserAddress.UserAddressId> {
    UserAddress findByUserIdAndAddressId(String userId, String addressId);
    Optional<UserAddress> findOneByUserIdAndIsDefault(String userId, Boolean isDefault);
    Boolean existsByUserIdAndIsDefault(String userId, Boolean isDefault);
    Boolean existsByUserIdAndAddressId(String userId, String addressId);
    Boolean existsByUserIdAndAddressIdAndIsDefault(String userId, String addressId, Boolean isDefault);

    List<UserAddress> findAllByUserId(String userId);
}
