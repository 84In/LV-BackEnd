package com.luanvan.userservice.command.service;


import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.userservice.command.command.ChangeDefaultAddressCommand;
import com.luanvan.userservice.command.command.CreateAddressCommand;
import com.luanvan.userservice.command.command.RemoveAddressCommand;
import com.luanvan.userservice.command.command.UpdateAddressCommand;
import com.luanvan.userservice.command.model.AddressChangeDefaultModel;
import com.luanvan.userservice.command.model.AddressCreateModel;
import com.luanvan.userservice.command.model.AddressRemoveModel;
import com.luanvan.userservice.command.model.AddressUpdateModel;
import com.luanvan.userservice.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
@Slf4j
public class AddressCommandService {
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAddressRepository userAddressRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private ProvinceRepository provinceRepository;
    @Autowired
    private DistrictRepository districtRepository;
    @Autowired
    private WardRepository wardRepository;

    public HashMap<?, ?> save(AddressCreateModel model) throws AppException {
        if (!userRepository.existsById(model.getUserId())) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (!provinceRepository.existsById(model.getProvinceId())) {
            throw new AppException(ErrorCode.PROVINCE_NOT_EXISTED);
        }
        if (!districtRepository.existsById(model.getDistrictId())) {
            throw new AppException(ErrorCode.DISTRICT_NOT_EXISTED);
        }
        if (model.getWardId() != null && !wardRepository.existsById(model.getWardId())) {
            throw new AppException(ErrorCode.WARD_NOT_EXISTED);
        }

        CreateAddressCommand command = new CreateAddressCommand(
                UUID.randomUUID().toString(),
                model.getName(),
                model.getPhone(),
                model.getHouseNumberAndStreet(),
                model.getProvinceId(),
                model.getDistrictId(),
                model.getWardId(),
                model.getUserId(),
                model.getIsDefault(),
                true
        );
        log.info("Send command create address: {}", command);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?,?> update(AddressUpdateModel model, String addressId) throws AppException {
        if (!userRepository.existsById(model.getUserId())) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (model.getProvinceId()!=null && !provinceRepository.existsById(model.getProvinceId())) {
            throw new AppException(ErrorCode.PROVINCE_NOT_EXISTED);
        }
        if (model.getDistrictId()!=null && !districtRepository.existsById(model.getDistrictId())) {
            throw new AppException(ErrorCode.DISTRICT_NOT_EXISTED);
        }
        if (model.getWardId() != null && !wardRepository.existsById(model.getWardId())) {
            throw new AppException(ErrorCode.WARD_NOT_EXISTED);
        }
        if (!addressRepository.existsById(addressId)) {
            throw new AppException(ErrorCode.ADDRESS_NOT_EXISTED);
        }
        if (!userAddressRepository.existsByUserIdAndAddressId(model.getUserId(),addressId)){
            throw new AppException(ErrorCode.ADDRESS_NOT_EXISTED);
        }
        UpdateAddressCommand command = new UpdateAddressCommand(
                addressId,
                model.getName(),
                model.getPhone(),
                model.getHouseNumberAndStreet(),
                model.getProvinceId(),
                model.getDistrictId(),
                model.getWardId(),
                model.getUserId(),
                model.getIsDefault(),
                true
                );
        log.info("Send command update address: {}", command);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?,?> changeDefault(String addressId, AddressChangeDefaultModel model) throws AppException {
        if(userAddressRepository.existsByUserIdAndAddressId(model.getUserId(),addressId)){
            throw new AppException(ErrorCode.ADDRESS_NOT_EXISTED);
        }
        ChangeDefaultAddressCommand command = new ChangeDefaultAddressCommand(addressId,model.getUserId(), model.getIsDefault());
        log.info("Send command change default address: {}", command);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?,?> remove(String addressId, AddressRemoveModel model) throws AppException {
        if (userAddressRepository.existsByUserIdAndAddressIdAndIsDefault(model.getUserId(), addressId,true)){
            throw new AppException(ErrorCode.ADDRESS_IS_DEFAULT);
        }
        RemoveAddressCommand command = new RemoveAddressCommand(addressId,model.getUserId());
        log.info("Send command remove address: {}", command);
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }


}
