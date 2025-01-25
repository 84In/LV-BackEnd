package com.luanvan.userservice.command.event;

import com.luanvan.userservice.command.data.*;
import com.luanvan.userservice.command.data.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class AddressEventsHandler {
    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private WardRepository wardRepository;

    @EventHandler
    public void on(AddressCreatedEvent event) {
        try {
            User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new RuntimeException("Not found user"));

            Address address = new Address();
            address.setId(event.getId());
            address.setPhone(event.getPhone());
            address.setHouseNumberAndStreet(event.getHouseNumberAndStreet());
            address.setIsActive(event.getIsActive());

            Province province = provinceRepository.findById(event.getProvinceId()).orElseThrow(() -> new RuntimeException("Not found province"));
            address.setProvince(province);

            District district = districtRepository.findById(event.getDistrictId()).orElseThrow(() -> new RuntimeException("Not found district"));
            address.setDistrict(district);

            if (event.getWardId() != null) {
                Ward ward = wardRepository.findById(event.getWardId()).orElseThrow(() -> new RuntimeException("Not found ward"));
                address.setWard(ward);
            } else {
                address.setWard(null);
            }

            Address addressResponse = addressRepository.save(address);

            UserAddress userAddress = new UserAddress();
            userAddress.setId(new UserAddress.UserAddressId(user.getId(), addressResponse.getId()));
            userAddress.setAddress(addressResponse);
            userAddress.setUser(user);
            userAddressRepository.save(userAddress);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
