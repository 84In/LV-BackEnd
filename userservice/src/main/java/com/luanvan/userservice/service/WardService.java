package com.luanvan.userservice.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.model.response.WardResponseModel;
import com.luanvan.userservice.entity.Ward;
import com.luanvan.userservice.repository.DistrictRepository;
import com.luanvan.userservice.repository.WardRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WardService {

    DistrictRepository districtRepository;

    WardRepository wardRepository;

    public WardResponseModel getWard(Integer id) {
        Ward ward = wardRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.WARD_NOT_EXISTED));
        WardResponseModel wardResponseModel = new WardResponseModel();
        wardResponseModel.setId(ward.getId());
        wardResponseModel.setName(ward.getName());
        wardResponseModel.setCodeName(ward.getCodeName());
        wardResponseModel.setDivisionType(ward.getDivisionType());
        return wardResponseModel;
    }

    public List<WardResponseModel> getWards() {
        List<Ward> wards = wardRepository.findAll();
        return mapWards(wards);
    }

    List<WardResponseModel> mapWards(List<Ward> wards) {
        List<WardResponseModel> wardResponseModels = new ArrayList<>();
        for (Ward ward : wards) {
            WardResponseModel wardResponseModel = new WardResponseModel();
            wardResponseModel.setId(ward.getId());
            wardResponseModel.setName(ward.getName());
            wardResponseModel.setCodeName(ward.getCodeName());
            wardResponseModel.setDivisionType(ward.getDivisionType());
            wardResponseModels.add(wardResponseModel);
        }
        return wardResponseModels;
    }

    public List<WardResponseModel> getWardsByDistrictId(Integer districtId) {
        if (!districtRepository.existsById(districtId)) {
            throw new AppException(ErrorCode.DISTRICT_NOT_EXISTED);
        }
        List<Ward> wards = wardRepository.findAllByDistrict_Id(districtId);
        return mapWards(wards);
    }
}
