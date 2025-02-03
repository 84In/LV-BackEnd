package com.luanvan.userservice.repository;

import com.luanvan.userservice.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UserAddress.UserAddressId> {
}
