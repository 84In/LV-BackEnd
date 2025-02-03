package com.luanvan.userservice.repository;

import com.luanvan.userservice.entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {
    boolean existsByCodeName(String codeName);
}
