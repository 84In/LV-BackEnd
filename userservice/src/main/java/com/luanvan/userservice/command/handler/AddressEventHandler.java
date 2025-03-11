package com.luanvan.userservice.command.handler;

import com.luanvan.userservice.command.event.AddressChangeDefaultEvent;
import com.luanvan.userservice.command.event.AddressCreatedEvent;
import com.luanvan.userservice.command.event.AddressRemoveEvent;
import com.luanvan.userservice.command.event.AddressUpdatedEvent;
import com.luanvan.userservice.entity.*;
import com.luanvan.userservice.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
public class AddressEventHandler {
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
            address.setName(event.getName());
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
            /* isDefault != null, giá trị isDefault phải là true và userId này chưa tồn tại địa chỉ default thì mới được gán true
            ngược lại là false
            */
            if (event.getIsDefault() != null && !userAddressRepository.existsByUserIdAndIsDefault(user.getId(), true)) {
                userAddress.setDefault(true);
            } else {
                userAddress.setDefault(false);
            }
            userAddress.setUser(user);
            userAddressRepository.save(userAddress);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /*
         Kiểm tra tồn tại giá trị trước khi thực hiện thay đổi
         Đối với mối quan hệ isDefautl phải kiểm tra xem có isDefault chưa nếu có rồi thì cập nhật thành false sau đớ mới gán lại isDefault cho địa chỉ mới
     */
    @EventHandler
    public void on(AddressUpdatedEvent event) {
        try {
            User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new RuntimeException("Not found user"));

            Address address = addressRepository.findById(event.getId()).orElseThrow(() -> new RuntimeException("Not found address"));


            if (event.getProvinceId() != null) {
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
            }

            if (event.getHouseNumberAndStreet() != null) {
                address.setHouseNumberAndStreet(event.getHouseNumberAndStreet());
            }

            if (event.getIsActive() != null) {
                address.setIsActive(event.getIsActive());
            }
            if (event.getName() != null) {
                address.setName(event.getName());
            }
            if (event.getPhone() != null) {
                address.setPhone(event.getPhone());
            }

            addressRepository.save(address);

            UserAddress userAddress = userAddressRepository.findByUserIdAndAddressId(user.getId(), address.getId());
            if (userAddress == null) {
                throw new RuntimeException("Not found userAddress");
            }

            if (event.getIsDefault() != null) {
                if (event.getIsDefault()) {
                    UserAddress oldDefaultUserAddress = userAddressRepository.findOneByUserIdAndIsDefault(user.getId(), true).orElseThrow(() -> new RuntimeException("Not found userAddress is default"));
                    if (oldDefaultUserAddress.getAddress().getId().equals(userAddress.getAddress().getId())) {
                        userAddress.setDefault(true);
                    } else {
                        oldDefaultUserAddress.setDefault(!oldDefaultUserAddress.isDefault());
                        userAddressRepository.save(oldDefaultUserAddress);
                        userAddress.setDefault(!userAddress.isDefault());
                    }
                } else {
                    if (userAddress.isDefault()) {
                        List<UserAddress> userAddresses = userAddressRepository.findAllByUserId(event.getUserId());
                        for (UserAddress userAddress1 : userAddresses){
                            if (!userAddress1.isDefault()) {
                                userAddress1.setDefault(true);
                                userAddressRepository.save(userAddress1);
                                userAddress.setDefault(event.getIsDefault());
                                break;
                            }
                        }
                    } else {
                        userAddress.setDefault(event.getIsDefault());
                    }
                }


            }
            userAddressRepository.save(userAddress);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @EventHandler
    public void on(AddressChangeDefaultEvent event) {
        try {
            UserAddress userAddress = userAddressRepository.findByUserIdAndAddressId(event.getUserId(), event.getId());
            if (userAddress == null) {
                throw new RuntimeException("Not found userAddress");
            }
            if (userAddress.isDefault()) { //true
                if (event.getIsDefault()) { //true
                    userAddress.setDefault(true);
                } else {
                    List<UserAddress> userAddresses = userAddressRepository.findAllByUserId(event.getUserId());
                    for (UserAddress userAddress1 : userAddresses){
                        if (!userAddress1.isDefault()) {
                            userAddress1.setDefault(true);
                            userAddressRepository.save(userAddress1);
                            userAddress.setDefault(event.getIsDefault());
                            break;
                        }
                    }

                }
            } else { //user address false
                if (event.getIsDefault()) {
                    UserAddress old = userAddressRepository.findOneByUserIdAndIsDefault(event.getUserId(), true).orElseThrow(() -> new RuntimeException("Not found userAddress is default"));
                    old.setDefault(false);
                    userAddressRepository.save(old);
                    userAddress.setDefault(event.getIsDefault());
                } else {
                    userAddress.setDefault(false);
                }

            }
            userAddressRepository.save(userAddress);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @EventHandler
    public void on(AddressRemoveEvent event) {
        try {
            User user = userRepository.findById(event.getUserId()).orElseThrow(() -> new RuntimeException("Not found user"));
            Address address = addressRepository.findById(event.getId()).orElseThrow(() -> new RuntimeException("Not found address"));
            if (userAddressRepository.existsByUserIdAndAddressIdAndIsDefault(user.getId(), address.getId(), true)) {
                throw new RuntimeException("User address is default not remove");
            }
            ;
            address.setIsActive(false);
            addressRepository.save(address);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
