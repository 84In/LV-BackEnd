package com.luanvan.userservice.command.data.repository;

import com.luanvan.userservice.command.data.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {

}
