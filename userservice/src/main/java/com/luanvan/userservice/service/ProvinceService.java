package com.luanvan.userservice.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.userservice.dto.ProvinceResponseModel;
import com.luanvan.userservice.entity.Province;
import com.luanvan.userservice.repository.ProvinceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProvinceService {

    ProvinceRepository provinceRepository;

    public ProvinceResponseModel getProvince(Integer provinceId) {
        Province province = provinceRepository.findById(provinceId).orElseThrow(()-> new AppException(ErrorCode.PROVINCE_NOT_EXISTED));
        ProvinceResponseModel responseModel = new ProvinceResponseModel();
        responseModel.setId(province.getId());
        responseModel.setName(province.getName());
        responseModel.setCodeName(province.getCodeName());
        responseModel.setDivisionType(province.getDivisionType());
        return responseModel;
    }

    public List<ProvinceResponseModel> getProvinces() {
        List<Province> provinces = provinceRepository.findAll();
        List<ProvinceResponseModel> responseModels = new ArrayList<>();
        for (Province province : provinces) {
            ProvinceResponseModel responseModel = new ProvinceResponseModel();
            responseModel.setId(province.getId());
            responseModel.setName(province.getName());
            responseModel.setCodeName(province.getCodeName());
            responseModel.setDivisionType(province.getDivisionType());
            responseModels.add(responseModel);
        }
        return responseModels;
    }



}
