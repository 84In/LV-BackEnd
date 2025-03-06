package com.luanvan.userservice.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.userservice.dto.DistrictResponseModel;
import com.luanvan.userservice.entity.District;
import com.luanvan.userservice.repository.DistrictRepository;
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
public class DistrictService {

    ProvinceRepository provinceRepository;

    DistrictRepository districtRepository;

    public List<DistrictResponseModel> getDistricts() {
        List<District> districts = districtRepository.findAll();
        return mapDistricts(districts);
    }

    public DistrictResponseModel getDistrictById(int id) {
        District district = districtRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.DISTRICT_NOT_EXISTED));
        DistrictResponseModel districtResponseModel = new DistrictResponseModel();
        districtResponseModel.setId(district.getId());
        districtResponseModel.setName(district.getName());
        districtResponseModel.setCodeName(district.getCodeName());
        districtResponseModel.setDivisionType(district.getDivisionType());
        return districtResponseModel;
    }

    public List<DistrictResponseModel> getDistrictByProvinceId(Integer provinceId) {
        if (!provinceRepository.existsById(provinceId)) {
            throw new AppException(ErrorCode.PROVINCE_NOT_EXISTED);
        }

        List<District> districts = districtRepository.findAllByProvince_Id(provinceId);
        return mapDistricts(districts);
    }

    List<DistrictResponseModel> mapDistricts(List<District> districts) {
        List<DistrictResponseModel> responseModels = new ArrayList<>();
        for (District district : districts) {
            DistrictResponseModel districtResponseModel = new DistrictResponseModel();
            districtResponseModel.setId(district.getId());
            districtResponseModel.setName(district.getName());
            districtResponseModel.setCodeName(district.getCodeName());
            districtResponseModel.setDivisionType(district.getDivisionType());
            responseModels.add(districtResponseModel);
        }
        return responseModels;
    }
}
