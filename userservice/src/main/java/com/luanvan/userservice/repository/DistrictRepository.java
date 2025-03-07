package com.luanvan.userservice.repository;

import com.luanvan.userservice.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findAllByProvince_Id(Integer provinceId);
}
