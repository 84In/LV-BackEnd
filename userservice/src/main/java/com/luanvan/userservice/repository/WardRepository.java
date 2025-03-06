package com.luanvan.userservice.repository;

import com.luanvan.userservice.entity.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WardRepository extends JpaRepository<Ward, Integer> {
    List<Ward> findAllByDistrict_Id(Integer districtId);
}
